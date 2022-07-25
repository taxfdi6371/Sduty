package com.d108.sduty.ui.main.timer

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.d108.sduty.R
import com.d108.sduty.common.NAVER_JOIN
import com.d108.sduty.databinding.FragmentTermsBinding
import com.d108.sduty.databinding.FragmentTimerBinding
import com.d108.sduty.ui.MainActivity
import com.d108.sduty.ui.main.timer.viewmodel.TimerViewModel
import com.d108.sduty.ui.sign.LoginFragmentDirections
import com.d108.sduty.ui.viewmodel.MainViewModel
import com.d108.sduty.utils.convertTimeLongToString
import com.d108.sduty.utils.safeNavigate
import com.d108.sduty.utils.showToast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

private const val TAG = "TimerFragment"
class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding

    private val mainViewModel : MainViewModel by activityViewModels()
    private val timerViewModel : TimerViewModel by viewModels()

    private lateinit var today : String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel.displayBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 첫 화면을 설정한다.
        initView()
        // 뷰모델 초기화한다.
        initViewModel()

    }

    // 화면 초기화
    private fun initView(){
        // 오늘 날짜
        today = convertTimeLongToString(Date(System.currentTimeMillis()), "yyyy년 M월 d일")

        binding.commonSelectedDate.text = today
        //timerViewModel.selectDate(today)
    }

    private fun showDatePicker(){
        val cal = Calendar.getInstance()
        // 날짜 선택 후 동작할 리스너
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            val selectedDate = "${year}년 ${month + 1}월 ${day}일"
            binding.commonSelectedDate.text = selectedDate
            //timerViewModel.selectDate(selectedDate)

            //
            when(selectedDate != today){
                true -> {
                    binding.apply {
                        btnReturnToday.text = "오늘($today) 로 돌아가기"
                        btnReturnToday.visibility = View.VISIBLE
                    }
                }
                false -> {
                    binding.btnReturnToday.visibility = View.INVISIBLE
                }
            }
        }
        // 다이얼로그 출력
        DatePickerDialog(requireActivity(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun initViewModel(){
        // 토스트 메시지 출력
        timerViewModel.toastMessage.observe(viewLifecycleOwner){ message ->
            requireContext().showToast(message)
        }

        binding.apply {
            // 날짜 선택
            commonSelectedDate.setOnClickListener {
                showDatePicker()
            }

            // 오늘 날짜로 돌아가기
            btnReturnToday.setOnClickListener {
                commonSelectedDate.text = today
                btnReturnToday.visibility = View.INVISIBLE
                //timerViewModel.selectDate(today)
            }
        }
    }
}
