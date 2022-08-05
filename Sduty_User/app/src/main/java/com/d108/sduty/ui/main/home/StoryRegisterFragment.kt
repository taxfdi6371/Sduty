package com.d108.sduty.ui.main.home

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.d108.sduty.R
import com.d108.sduty.databinding.FragmentStoryRegisterBinding
import com.d108.sduty.model.dto.InterestHashtag
import com.d108.sduty.model.dto.JobHashtag
import com.d108.sduty.model.dto.Story
import com.d108.sduty.ui.main.home.viewmodel.HomeViewModel
import com.d108.sduty.ui.sign.dialog.TagSelectDialog
import com.d108.sduty.ui.sign.viewmodel.TagViewModel
import com.d108.sduty.ui.viewmodel.MainViewModel
import com.d108.sduty.ui.viewmodel.StoryViewModel
import com.d108.sduty.utils.UriPathUtil
import com.d108.sduty.utils.navigateBack
import com.d108.sduty.utils.safeNavigate
import com.d108.sduty.utils.showToast
import com.github.dhaval2404.imagepicker.ImagePicker

//게시물 등록 - 글 내용입력, 이미지 추가/ 미리보기, 카메라 or 이미지 선택, 태그 선택
private const val TAG ="StoryRegisterFragment"
class StoryRegisterFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentStoryRegisterBinding
    private val viewModel: HomeViewModel by viewModels()
    private val storyViewModel: StoryViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val tagViewModel: TagViewModel by activityViewModels()
    // (공개 범위) 0 : 전체 공개, 1 : 팔로워만, 2 : 나만 보기
    private var disclosure = 0
    private val args: StoryRegisterFragmentArgs by navArgs()
    private var imageUrl: String? = null

    private lateinit var selectedJobList: MutableList<JobHashtag>
    private lateinit var selectedInterestList: MutableList<InterestHashtag>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryRegisterBinding.inflate(inflater, container, false)
        binding.apply {
            etWrite.addTextChangedListener(object : TextWatcher {
                // 입력이 끝날 때 동작
                override fun afterTextChanged(p0: Editable?) {}
                // 입력하기 전에 동작
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                // 타이핑되는 텍스트에 변화가 있으면 동작
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tvWordLength.text = "${etWrite.length()} / 200"
                    if (etWrite.length() > 200) {
                        //requireContext().showToast("최대 200자까지 입력 가능합니다.")
                        etWrite.setTextColor(Color.RED)
                        tvWordLength.setTextColor(Color.RED)
                    }
                    else {
                        etWrite.setTextColor(Color.BLACK)
                        tvWordLength.setTextColor(Color.BLACK)
                    }
                }
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()
        initViewModel()
        if(args.storyImage != null){
            viewModel.setStoryImage(args.storyImage)
        }
    }

    private fun initViewModel() {

    }

    private fun initView(){
        val startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result : ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data
                if (resultCode == Activity.RESULT_OK) {
                    // Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    // mProfileUri = fileUri
                    binding.apply {
                        //imgStory.setImageURI(fileUri)
                        imageUrl = UriPathUtil().getPath(requireContext(), fileUri)
                        Log.d(TAG, "initView: ${imageUrl}")
                        findNavController().safeNavigate(
                            StoryRegisterFragmentDirections
                                .actionStoryRegisterFragmentToStoryDecoFragment(fileUri.toString())
                        )
                    }
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    requireContext().showToast(ImagePicker.getError(data))
                } else {
                    requireContext().showToast("Task Cancelled")
                }
            }

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@StoryRegisterFragment
            // 공개 범위 설정 버튼 클릭 시, 팝업 메뉴 보이기
            btnDisclosure.setOnClickListener {
                PopupMenu(requireContext(), it).apply {
                    // implements OnMenuItemClickListener
                    setOnMenuItemClickListener(this@StoryRegisterFragment)
                    inflate(R.menu.disclosure_menu)
                    show()
                }
            }
            btnAddImg.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop(3f, 4f)	    //Crop image and let user choose aspect ratio.
                    .compress(1024)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
            imgStory.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop(3f, 4f)	    //Crop image and let user choose aspect ratio.
                    .compress(1024)
                    .createIntent {
                        startForProfileImageResult.launch(it)
                    }
            }
            ivBack.setOnClickListener {
                navigateBack(requireActivity())
            }
            ivRegisterStory.setOnClickListener {
                // 게시물 정보 등록
                // 등록할 때, 초기 화면으로 visibility 다시 세팅...
                if(viewModel.bitmap.value == null){
                    requireContext().showToast("사진을 등록해 주세요")
                }else if(etWrite.text.isEmpty()) {
                    requireContext().showToast("내용을 입력해 주세요")
                }else{
                    storyViewModel.insertStory(Story(mainViewModel.user.value!!.seq, "", etWrite.text.toString(), disclosure), viewModel.bitmap.value!!)
                    requireContext().showToast("스토리가 등록 되었습니다")
                    findNavController().safeNavigate(StoryRegisterFragmentDirections.actionStoryRegisterFragmentToTimeLineFragment())
                }
            }
            btnAddSubject.setOnClickListener {
                TagSelectDialog(requireContext()).let {
                    it.show(parentFragmentManager, null)
                    it.onClickConfirm = object : TagSelectDialog.OnClickConfirm{
                        override fun onClick(selectedJobList: MutableList<JobHashtag>, selectedInterestList: MutableList<InterestHashtag>) {
                            this@StoryRegisterFragment.selectedJobList = selectedJobList
                            this@StoryRegisterFragment.selectedInterestList = selectedInterestList
                        }
                    }
                }
            }

        }
    }

    // 팝업 메뉴 클릭 이벤트 설정
    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.privateDisclosure -> {
                disclosure = 0
                binding.btnDisclosure.text = "나만 보기"
//                requireContext().showToast("나만 보기 클릭 : " + disclosure)
                true
            }
            R.id.followerDisclosure -> {
                disclosure = 1
                binding.btnDisclosure.text = "팔로워 공개"
//                requireContext().showToast("팔로워만 공개 클릭 : " + disclosure)
                true
            }
            R.id.publicDisclosure -> {
                disclosure = 2
                binding.btnDisclosure.text = "전체 공개"
//                requireContext().showToast("전체 공개 클릭 : " + disclosure)
                true
            }
            else -> false
        }
    }
}
