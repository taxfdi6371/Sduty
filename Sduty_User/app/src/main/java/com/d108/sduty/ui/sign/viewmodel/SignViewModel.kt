package com.d108.sduty.ui.sign

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.sduty.common.ApplicationClass
import com.d108.sduty.model.Retrofit
import com.d108.sduty.model.dto.AuthInfo
import com.d108.sduty.model.dto.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.nurigo.sdk.message.model.Message
import net.nurigo.sdk.message.request.SingleMessageSendingRequest
import java.lang.Exception

private const val TAG ="SignViewModel"
class SignViewModel: ViewModel() {
    private val _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token
    fun setAccessToken(token: String){
        _token.postValue(token)
    }

    private val _isLoginSucceed = MutableLiveData<Boolean>()
    val isLoginSucceed: LiveData<Boolean>
        get() = _isLoginSucceed

    fun login(id: String, pw: String){
        viewModelScope.launch(Dispatchers.IO){
            val user = User(id, pw)
            Retrofit.userApi.login(user).let {
                if(it.isSuccessful && it.body() != null){
                    _user.postValue(it.body())
                    _isLoginSucceed.postValue(true)
                }else{
                    _isLoginSucceed.postValue(false)
                    Log.d(TAG, "login: ${it}")
                }
            }
        }
    }

    private val _isExistKakaoAccount = MutableLiveData<Boolean>()
    val isExistKakaoAccount: LiveData<Boolean>
        get() = _isExistKakaoAccount

    fun kakaoLogin(token: String){
        viewModelScope.launch(Dispatchers.IO){
            Log.d(TAG, "kakaoLogin: ")
            Retrofit.userApi.kakaoLogin(token).let {
                if(it.code() == 401){ // UNAUTHORIZED
                    _isExistKakaoAccount.postValue(false)
                    Log.d(TAG, "kakaoLogin: ${it}")

                }else if(it.isSuccessful && it.body() != null){
                    _user.postValue(it.body())
                    _isLoginSucceed.postValue(true)
                }
                else{
                    Log.d(TAG, "kakaoLogin: ${it}")
                }
            }
        }
    }

    private val _isExistNaverAccount = MutableLiveData<Boolean>()
    val isExistNaverAccount: LiveData<Boolean>
        get() = _isExistNaverAccount

    fun naverLogin(token: String){
        viewModelScope.launch(Dispatchers.IO){
            Retrofit.userApi.naverLogin(token).let {
                if(it.code() == 401){ // UNAUTHORIZED
                    _isExistNaverAccount.postValue(false)
                    Log.d(TAG, "naverLogin: ${it}")

                }else if(it.isSuccessful && it.body() != null){
                    _user.postValue(it.body())
                    _isLoginSucceed.postValue(true)
                }
                else{
                    Log.d(TAG, "naverLogin: ${it}")
                }
            }
        }
    }

    private val _isJoinSucced = MutableLiveData<Boolean>()
    val isJoinSucced: LiveData<Boolean>
        get() = _isJoinSucced

    fun join(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Retrofit.userApi.join(user).let {
                    Log.d("TAG", "join: ${it}")
                    if(it.isSuccessful){
                        _isJoinSucced.postValue(true)
                    }
                    else if (it.code() == 500) {
                        _isJoinSucced.postValue(false)
                    }
                }
            } catch (e: Exception){
                Log.d("TAG", "login: error ${e.message}")
            }
        }
    }

    fun kakaoJoin(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                Retrofit.userApi.kakaoJoin(token.value.toString()).let {
                    if(it.isSuccessful && it.body() != null){
                        _isJoinSucced.postValue(true)
                    }else{
                        _isJoinSucced.postValue(false)
                    }
                }
            }catch (e: Exception){
                Log.d(TAG, "kakaoJoin: ${e.message}")
            }
        }
    }
    fun naverJoin(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                Retrofit.userApi.naverJoin(token.value.toString()).let {
                    if(it.isSuccessful && it.body() != null){
                        _isJoinSucced.postValue(true)
                    }else{
                        _isJoinSucced.postValue(false)
                    }
                }
            }catch (e: Exception){
                Log.d(TAG, "kakaoJoin: ${e.message}")
            }
        }
    }


    private val _authInfo = MutableLiveData<AuthInfo>()
    val authInfo: LiveData<AuthInfo>
        get() = _authInfo

    private val _isSucceedSendAuthInfo = MutableLiveData<Boolean>()
    val isSucceedSendAuthInfo : LiveData<Boolean>
        get() = _isSucceedSendAuthInfo

    fun sendOTP(userPhoneNum: String){
        val authCode = (100000..999999).random()
        val message = Message(
            from = "01049177914",
            to = userPhoneNum,
            text = "[Sduty] 인증 번호[${authCode}]를 입력해 주세요. "
        )
        viewModelScope.launch(Dispatchers.IO){
            val newAuthInfo = AuthInfo(authCode.toString(), userPhoneNum)
            _authInfo.postValue(newAuthInfo)
            ApplicationClass.messageService.sendOne(SingleMessageSendingRequest(message)) // SMS 보냄
            Retrofit.userApi.sendAuthInfo(newAuthInfo).let {
                if(it.code() == 200){
                    _isSucceedSendAuthInfo.postValue(true)
                }else{
                    _isSucceedSendAuthInfo.postValue(false)
                }
            }
        }
    }

    private val _isSucceedAuth = MutableLiveData<Boolean>()
    val isSucceedAuth : LiveData<Boolean>
        get() = _isSucceedAuth
    private val _toastMsg = MutableLiveData<String>()
    val toastMsg : LiveData<String>
        get() = _toastMsg
    fun checkOTP(inputCode: String){
        viewModelScope.launch(Dispatchers.IO){
            val code = authInfo.value
            code!!.authcode = inputCode
            Retrofit.userApi.checkAuthCode(code).let {
                if(it.code() == 401){
                    _isSucceedAuth.postValue(false)
                    _toastMsg.postValue("인증번호가 일치하지 않습니다.")
                }
                else if(it.code() == 410){
                    _isSucceedAuth.postValue(false)
                    _toastMsg.postValue("인증 시간이 만료되었습니다.")
                }
                else if(it.isSuccessful){
                    _isSucceedAuth.postValue(true)
                }
            }
        }

    }

}