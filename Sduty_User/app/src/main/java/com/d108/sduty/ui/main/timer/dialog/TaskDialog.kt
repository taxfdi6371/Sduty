package com.d108.sduty.ui.main.timer.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.d108.sduty.R
import com.d108.sduty.databinding.DialogTaskBinding
import com.d108.sduty.model.dto.Report
import com.d108.sduty.model.dto.Task
import com.d108.sduty.ui.main.timer.viewmodel.TimerViewModel
import com.d108.sduty.utils.convertTimeDateToString
import com.d108.sduty.utils.getDeviceSize
import java.util.*

class TaskDialog : DialogFragment() {
    private lateinit var binding : DialogTaskBinding
    private val timerViewModel: TimerViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여백 터치 시 다이얼로그 종료 방지
        isCancelable = false

        val action = arguments?.getString("Action","Add")

        when(action){
            "Add" -> {
                addTask()
            }
            "Info" -> {
                infoTask()
            }
            "Modify" -> {
                modifyTask()
            }
        }
    }

    private fun addTask(){
        binding.apply {
            val time = timerViewModel.timer.value!!
            val hour = time / 60 / 60
            val min = time / 60
            val sec = time % 60
            tvTimer.text = String.format("%02d:%02d:%02d",hour,min, sec)

            etStartTime.setText(timerViewModel.startTime)
            etStartTime.isEnabled = false

            etEndTime.setText(convertTimeDateToString(Date(System.currentTimeMillis()),"hh:mm:ss"))
            etEndTime.isEnabled = false

            // 내용 2, 3 추가 버튼
            ivAddContent.setOnClickListener {
                if(etContent2.visibility == View.GONE && etContent3.visibility == View.GONE){
                    etContent2.visibility = View.VISIBLE
                    ivRemoveContent2.visibility = View.VISIBLE
                } else if(etContent2.visibility == View.VISIBLE && etContent3.visibility == View.GONE){
                    ivAddContent.visibility = View.INVISIBLE
                    ivRemoveContent2.visibility = View.INVISIBLE
                    etContent3.visibility = View.VISIBLE
                    ivRemoveContent3.visibility = View.VISIBLE
                }
            }

            // 내용 2
            etContent2.visibility = View.GONE
            ivRemoveContent2.visibility = View.GONE
            ivRemoveContent2.setOnClickListener {
                etContent2.setText("")
                etContent2.visibility = View.GONE
                ivRemoveContent2.visibility = View.GONE
            }

            // 내용 3
            etContent3.visibility = View.GONE
            ivRemoveContent3.visibility = View.GONE
            ivRemoveContent3.setOnClickListener {
                ivAddContent.visibility = View.VISIBLE
                ivRemoveContent2.visibility = View.VISIBLE
                etContent3.setText("")
                etContent3.visibility= View.GONE
                ivRemoveContent3.visibility = View.GONE
            }

            btnSave.setOnClickListener {
                // 등록 수행
                val startTime = etStartTime.text.toString()
                val endTime = etEndTime.text.toString()
                val title = etTitle.text.toString()
                val content1 = etContent1.text.toString()
                val content2 = etContent2.text.toString()
                val content3 = etContent3.text.toString()

                val today = convertTimeDateToString(Date(System.currentTimeMillis()), "yyyy-MM-dd")

                val newTask = Task(-1,-1,endTime, startTime, 0, title, content1, content2, content3)

                // TODO: userSeq 변경 필요
                val report = Report(-1, 47, today, "",listOf(newTask))

                // title 은 필수로 입력해야한다.
                if(title.isNotEmpty()){
                    timerViewModel.saveTask(report)
                    timerViewModel.stopTimer()
                    dismiss()
                } else {
                    ConfirmDialog().apply {
                        // 경고창에 출력할 메시지를 담아 보낸다.
                        arguments = Bundle().apply {
                            putString("Action","Error")
                            putString("Message","제목을 입력해주세요!!")
                        }
                        show(this@TaskDialog.requireActivity().supportFragmentManager, "ConfirmDialog")
                    }
                }
            }

            btnModify.visibility = View.GONE

            btnDelete.setOnClickListener {
                ConfirmDialog().apply {
                    // 삭제 경고
                    arguments = Bundle().apply {
                        putString("Action","RemoveTimer")
                    }
                    show(this@TaskDialog.requireActivity().supportFragmentManager, "ConfirmDialog")
                }
            }
        }

        timerViewModel.resetDelayTimer()
        timerViewModel.stopTimer()
    }

    private fun infoTask(){
        val position = arguments?.getInt("Position", 0)
        val task = timerViewModel.report.value!!.tasks[position!!]
        binding.apply {
            val hour = task.durationTime / 60 / 60
            val min = (task.durationTime / 60) % 60
            val sec = task.durationTime % 60

            tvTimer.text = String.format("%02d:%02d:%02d",hour,min,sec)

            etStartTime.setText(task.startTime)
            etStartTime.isEnabled = false

            etEndTime.setText(task.endTime)
            etEndTime.isEnabled = false

            etTitle.setText(task.title)
            etTitle.isEnabled = false

            etContent1.setText(task.content1)
            etContent1.isEnabled = false
            ivAddContent.visibility = View.INVISIBLE

            etContent2.setText(task.content2)
            if(task.content2.isNotEmpty()){
                etContent2.visibility = View.VISIBLE
            } else {
                etContent2.visibility = View.GONE
            }
            etContent2.isEnabled = false
            ivRemoveContent2.visibility = View.GONE

            etContent3.setText(task.content3)
            if(task.content3.isNotEmpty()){
                etContent3.visibility = View.VISIBLE
            } else {
                etContent3.visibility = View.GONE
            }
            etContent3.isEnabled = false
            ivRemoveContent3.visibility = View.GONE

            btnDelete.setOnClickListener {
                ConfirmDialog().apply {
                    arguments = Bundle().apply {
                        putString("Action","RemoveTask")
                        putInt("Position", position)
                    }
                    show(this@TaskDialog.requireActivity().supportFragmentManager, "TaskDialog")
                }
            }

            btnModify.visibility = View.VISIBLE
            btnModify.setOnClickListener {
                modifyTask()
            }

            btnSave.text = "확인"
            btnSave.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun modifyTask(){
        val position = arguments?.getInt("Position", 0)
        binding.apply {
            if (etContent2.visibility == View.GONE && etContent3.visibility == View.GONE){
                ivAddContent.visibility = View.VISIBLE
            } else if(etContent2.visibility == View.VISIBLE && etContent3.visibility == View.GONE){
                ivAddContent.visibility = View.VISIBLE
                ivRemoveContent2.visibility = View.VISIBLE
            } else {
                ivAddContent.visibility = View.INVISIBLE
                ivRemoveContent2.visibility = View.INVISIBLE
                ivRemoveContent3.visibility = View.VISIBLE
            }

            // 내용 2, 3 추가 버튼
            ivAddContent.setOnClickListener {
                if(etContent2.visibility == View.GONE && etContent3.visibility == View.GONE){
                    etContent2.visibility = View.VISIBLE
                    ivRemoveContent2.visibility = View.VISIBLE
                } else if(etContent2.visibility == View.VISIBLE && etContent3.visibility == View.GONE) {
                    ivAddContent.visibility = View.INVISIBLE
                    ivRemoveContent2.visibility = View.INVISIBLE
                    etContent3.visibility = View.VISIBLE
                    ivRemoveContent3.visibility = View.VISIBLE
                }
            }

            // 내용 2
            ivRemoveContent2.setOnClickListener {
                etContent2.setText("")
                etContent2.visibility = View.GONE
                ivRemoveContent2.visibility = View.GONE
            }

            // 내용 3
            ivRemoveContent3.setOnClickListener {
                ivAddContent.visibility = View.VISIBLE
                ivRemoveContent2.visibility = View.VISIBLE
                etContent3.setText("")
                etContent3.visibility= View.GONE
                ivRemoveContent3.visibility = View.GONE
            }

            etContent1.isEnabled = true
            etContent2.isEnabled = true
            etContent3.isEnabled = true

            btnDelete.text = "취소"
            btnDelete.setOnClickListener {
                dismiss()
            }

            btnModify.visibility = View.GONE

            btnSave.text = "저장"
            btnSave.setOnClickListener {
                var task = timerViewModel.report.value!!.tasks[position!!]

                task.content1 = etContent1.text.toString()
                task.content2 = etContent2.text.toString()
                task.content3 = etContent3.text.toString()

                timerViewModel.modifyTask(task)
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes

        // 앱을 실행한 디바이스의 가로, 세로 크기를 가져온다.
        val deviceWidth = getDeviceSize(requireActivity()).x

        // 다이얼로그 크기를 디바이스 가로의 90%로 설정한다.
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}