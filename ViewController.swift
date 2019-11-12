//
//  ViewController.swift
//  NICU_dbReader
//
//  Modified by Chris Lee.
//  Using AudioKit to utilize iphone microphone to read sound input
//
//  For the use of measuring the ambient sound in the
//  Neonatal Intensive Care Unit.
//  If decibel reading exceeds threshold the user will be alerted via
//  vibration of the phone.
//
//  AudioKit
//  Created by Kanstantsin Linou, revision history on Githbub.
//  Copyright © 2018 AudioKit. All rights reserved.
//

import AudioKit
import AudioKitUI
import UIKit
import AudioToolbox

class ViewController: UIViewController {
    
    @IBOutlet private var frequencyLabel: UILabel!
    @IBOutlet private var amplitudeLabel: UILabel!
    @IBOutlet private var decibelsLabel: UILabel!
    @IBOutlet private var audioInputPlot: EZAudioPlot!
    @IBOutlet weak var calibrationValue: UILabel!
    @IBOutlet weak var submitButton: UIButton!
    @IBOutlet weak var thresholdLabel: UITextField!

    var mic: AKMicrophone!
    var tracker: AKFrequencyTracker!
    var silence: AKBooster!
    var decibels = 0.0
    var listenTimer : Timer!
    //Threshold value if decibel reading is greater than the threshold
    //then alert user
    var DB_THRESHHOLD = 50
    var calibrate = 0
    //save user settings
    let preferences = UserDefaults.standard
    //using stepper to increment calibrate
    //@var Int calibrate used to add or subtract to decibel reading
    @IBAction func changeCalibrationValue(_ sender: UIStepper) {
        calibrationValue.text = String(sender.value)
        calibrate = (calibrationValue.text! as NSString).integerValue
        UserDefaults.standard.set(String(sender.value), forKey: "calibrationSetting")
    }
    
    //submit the value in the thresholdLabel textfield to DB_THRESHOLD
    @IBAction func submit_thresh(_ sender: Any) {
        frequencyLabel.text = String(thresholdLabel.text!)
        DB_THRESHHOLD = (thresholdLabel.text! as NSString).integerValue
        UserDefaults.standard.set(DB_THRESHHOLD, forKey: "thresholdSetting")
        let submit_alert = UIAlertController(title: "Threshold Changed", message: "", preferredStyle: .alert)
        submit_alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: { _ in NSLog("The \"OK\" alert occured.")
        }))
        self.present(submit_alert, animated: true, completion: nil)
    }
    
    let noteFrequencies = [16.35, 17.32, 18.35, 19.45, 20.6, 21.83, 23.12, 24.5, 25.96, 27.5, 29.14, 30.87]

    func setupPlot() {
        let plot = AKNodeOutputPlot(mic, frame: audioInputPlot.bounds)
        plot.translatesAutoresizingMaskIntoConstraints = false
        plot.plotType = .rolling
        plot.shouldFill = true
        plot.shouldMirror = true
        plot.color = UIColor.blue
        audioInputPlot.addSubview(plot)

        // Pin the AKNodeOutputPlot to the audioInputPlot
        var constraints = [plot.leadingAnchor.constraint(equalTo: audioInputPlot.leadingAnchor)]
        constraints.append(plot.trailingAnchor.constraint(equalTo: audioInputPlot.trailingAnchor))
        constraints.append(plot.topAnchor.constraint(equalTo: audioInputPlot.topAnchor))
        constraints.append(plot.bottomAnchor.constraint(equalTo: audioInputPlot.bottomAnchor))
        constraints.forEach { $0.isActive = true }
    }
    // adding done button to the toolbar of the numberpad
    func addDoneButton() {
        let keyboardToolbar = UIToolbar()
        keyboardToolbar.sizeToFit()
        let flexBarButton = UIBarButtonItem(barButtonSystemItem: .flexibleSpace,
                                            target: nil, action: nil)
        let doneBarButton = UIBarButtonItem(barButtonSystemItem: .done,
                                            target: view, action: #selector(UIView.endEditing(_:)))
        keyboardToolbar.items = [flexBarButton, doneBarButton]
        thresholdLabel.inputAccessoryView = keyboardToolbar
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        thresholdLabel.text = String(50)
        calibrationValue.text = String(0.0)
        addDoneButton()
        AKSettings.audioInputEnabled = true
        mic = AKMicrophone()
        tracker = AKFrequencyTracker(mic)
        silence = AKBooster(tracker, gain: 0)
        
        if let savedCalibration = UserDefaults.standard.object(forKey: "calibrationSetting") as? String {
            calibrationValue.text = savedCalibration
        }
        if let savedThresh = UserDefaults.standard.object(forKey: "thresholdSetting") as? Int {
            DB_THRESHHOLD = savedThresh
            frequencyLabel.text = String(savedThresh)
            thresholdLabel.text = String(savedThresh)
        }
        frequencyLabel.text = String(DB_THRESHHOLD)
        
        //moving the textfield
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }

    //move textfield with the keyboard
    @objc func keyboardWillShow(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y == 0 {
                self.view.frame.origin.y -= keyboardSize.height
            }
        }
    }
    //hide keyboard
    @objc func keyboardWillHide(notification: NSNotification) {
        if self.view.frame.origin.y != 0 {
            self.view.frame.origin.y = 0
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        AudioKit.output = silence
        do {
            try AudioKit.start()
        } catch {
            AKLog("AudioKit did not start!")
        }
        setupPlot()
    }
    
    //toggle button
    @IBAction func listen(_ sender: RecordButton) {
        if (sender.isON) {
            //scheduler for udateUI()
            listenTimer = Timer.scheduledTimer(timeInterval: 0.3,
                                 target: self,
                                 selector: #selector(ViewController.updateUI),
                                 userInfo: nil,
                                 repeats: true)
        } else {
            listenTimer.invalidate()
            amplitudeLabel.text = String(0)
            decibelsLabel.text = String(0)
        }
    }
    
    @objc func updateUI() {
        if tracker.amplitude > 0.0 {
            //frequencyLabel.text = String(50)
            var frequency = Float(tracker.frequency)
            while frequency > Float(noteFrequencies[noteFrequencies.count - 1]) {
                frequency /= 2.0
            }
            while frequency < Float(noteFrequencies[0]) {
                frequency *= 2.0
            }
            var minDistance: Float = 10_000.0
            var index = 0
            for i in 0..<noteFrequencies.count {
                let distance = fabsf(Float(noteFrequencies[i]) - frequency)
                if distance < minDistance {
                    index = i
                    minDistance = distance
                }
            }
        }
        /**
         *DB calculation
         *
         */
        decibels = abs(20 * log10(tracker.amplitude/0.3) + 70 + 4) + self.calibrate
        print("decibels \(self.decibels)")
        
        vibrateAlert()
        
        amplitudeLabel.text = String(format: "%0.2f", tracker.amplitude)
        
        
        decibelsLabel.text = "\(String(describing: decibels))"
    }
    
    func vibrate() {
        /* multiple vibrations
        for _ in 1...5 {
            AudioServicesPlayAlertSound(kSystemSoundID_Vibrate)
            usleep(500000)
        }
        */
    
        //Vibrate once
        AudioServicesPlayAlertSound(kSystemSoundID_Vibrate)
    }
    
    //display alert when the db reading is past the threshold (DB_THRESHOLD)
    //to prevent vibration interference when listening, the phone will not vibrate until
    //AudioKit listening has stopped.
    //if the Db reading exceeds the threshold AudioKit will be stopped to allow vibration
    //then immediately resumed.
    func vibrateAlert() {
        if (Int(decibels) >= DB_THRESHHOLD) {
            do {
                try AudioKit.stop()
                vibrate()
                try AudioKit.start()
            } catch {
                AKLog("AudioKit did not stop")
            }
            print("\(decibels)")
            print("did vibrate")
            let alert = UIAlertController(title: "TOO LOUD", message: "Decibel Reading: \(decibels).", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: { _ in NSLog("The \"OK\" alert occured.")
            }))
            alert.addAction(UIAlertAction(title: NSLocalizedString("STOP", comment: "Default action"), style: .default, handler: { action in self.listenTimer.invalidate()
            }))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    @IBAction func didTapInputDevicesButton(_ sender: UIBarButtonItem) {
        let inputDevices = InputDeviceTableViewController()
        inputDevices.settingsDelegate = self
        let navigationController = UINavigationController(rootViewController: inputDevices)
        navigationController.preferredContentSize = CGSize(width: 300, height: 300)
        navigationController.modalPresentationStyle = .popover
        navigationController.popoverPresentationController!.delegate = self
        self.present(navigationController, animated: true, completion: nil)
    }

}

extension ViewController: UIPopoverPresentationControllerDelegate {

    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle {
        return .none
    }

    func prepareForPopoverPresentation(_ popoverPresentationController: UIPopoverPresentationController) {
        popoverPresentationController.permittedArrowDirections = .up
        popoverPresentationController.barButtonItem = navigationItem.rightBarButtonItem
    }
}

// InputDeviceDelegate

extension ViewController: InputDeviceDelegate {

    func didSelectInputDevice(_ device: AKDevice) {
        do {
            try mic.setDevice(device)
        } catch {
            AKLog("Error setting input device")
        }
    }

}


