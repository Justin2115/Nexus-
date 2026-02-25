package com.example.nexus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nexus.data.PatientStatus
import com.example.nexus.data.RoomManager
import com.example.nexus.databinding.FragmentPatientAssistantBinding
import java.util.Locale

class PatientAssistantFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentPatientAssistantBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private var isListening = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientAssistantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tts = TextToSpeech(requireContext(), this)
        setupSpeechRecognizer()

        binding.fabMic.setOnClickListener {
            if (checkPermission()) {
                startListening()
            } else {
                requestPermission()
            }
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.tvSpeechOutput.text = "Listening..."
                binding.fabMic.text = "LISTENING..."
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                binding.fabMic.text = "TAP TO SPEAK"
            }
            override fun onError(error: Int) {
                binding.tvSpeechOutput.text = "Error: $error"
                binding.fabMic.text = "TAP TO SPEAK"
                isListening = false
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val command = matches[0].lowercase()
                    binding.tvSpeechOutput.text = "You said: $command"
                    processCommand(command)
                }
                isListening = false
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startListening() {
        if (isListening) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.startListening(intent)
        isListening = true
    }

    private fun processCommand(command: String) {
        val bed = RoomManager.beds.find { it.bedNumber == "101" } ?: return // Simulation for Bed 101
        
        var response = ""
        when {
            command.contains("call nurse") -> {
                bed.status = PatientStatus.ATTENTION_NEEDED
                bed.lastRequest = "Call nurse"
                response = "Calling the nurse. They will be with you shortly."
                RoomManager.addLog(bed.bedNumber, "Voice Command", "Patient requested: Call nurse")
            }
            command.contains("water") -> {
                bed.status = PatientStatus.ATTENTION_NEEDED
                bed.lastRequest = "I need water"
                response = "I have informed the nurse that you need water."
                RoomManager.addLog(bed.bedNumber, "Voice Command", "Patient requested: Water")
            }
            command.contains("pain") -> {
                bed.status = PatientStatus.EMERGENCY
                bed.lastRequest = "Pain alert"
                response = "I'm sorry to hear that. I've sent an emergency alert to the nurse station."
                RoomManager.addLog(bed.bedNumber, "Voice Command", "Patient reported: Pain")
            }
            command.contains("light") -> {
                bed.lightOn = command.contains("on")
                response = if (bed.lightOn) "Turning the lights on." else "Turning the lights off."
                RoomManager.addLog(bed.bedNumber, "Voice Command", response)
            }
            command.contains("help") -> {
                bed.status = PatientStatus.EMERGENCY
                bed.lastRequest = "Help requested"
                response = "Emergency help requested. Station alerted."
                RoomManager.addLog(bed.bedNumber, "Voice Command", "Patient requested: Help")
                com.example.nexus.data.AlertManager.registerHelpRequest(bed.bedNumber)
            }
            else -> {
                response = "I'm sorry, I didn't catch that. Could you repeat?"
            }
        }
        
        binding.tvAiReply.text = response
        speak(response)
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer.destroy()
        tts.stop()
        tts.shutdown()
        _binding = null
    }
}
