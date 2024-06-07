package com.kodonho.aroc

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kodonho.aroc.api.MemberInfoUpdate
import com.kodonho.aroc.dto.MemberInfoUpdateDto
import com.kodonho.aroc.RetrofitInstance.RetrofitBuilder
import com.kodonho.aroc.api.MemberView
import com.kodonho.aroc.dto.MemberViewDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoUpdateActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var userTypeRadioGroup: RadioGroup
    private lateinit var updateButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    companion object {
        private const val TAG = "UserInfoUpdateActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_update)

        // View 초기화
        emailEditText = findViewById(R.id.signupEmail)
        passwordEditText = findViewById(R.id.signupPwd)
        nameEditText = findViewById(R.id.signupName)
        phoneEditText = findViewById(R.id.signupPhone)
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup)
        updateButton = findViewById(R.id.signupBtn)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Retrofit 초기화
        RetrofitBuilder.initialize(applicationContext)

        // SharedPreferences에서 이메일 가져오기
        val email = sharedPreferences.getString("user_email", null)
        Log.d(TAG, "SharedPreferences에 저장된 이메일: $email")

        if (email != null) {
            // 회원 정보 가져오기
            fetchMemberInfo(email)
        } else {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            // 로그인 화면으로 이동하는 로직 추가
        }

        // 버튼 클릭 리스너 설정
        updateButton.setOnClickListener {
            updateUserInformation()
        }


    }

    //  사용자 회원정보 불러오기
    private fun fetchMemberInfo(email: String) {
        val service = RetrofitBuilder.createService(MemberView::class.java)
        val call = service.getMemberInfo(email)

        call.enqueue(object : Callback<MemberViewDto> {
            override fun onResponse(call: Call<MemberViewDto>, response: Response<MemberViewDto>) {
                if (response.isSuccessful) {
                    val memberInfo = response.body()
                    memberInfo?.let {
                        Log.d(TAG, "회원정보: $it")
                        emailEditText.setText(it.email)
                        nameEditText.setText(it.name)
                        phoneEditText.setText(it.phone)
                        // 비밀번호는 보안상 표시하지 않습니다.
                        // userType 설정 (RadioButton 선택)
                        if (it.userType == 1) {
                            userTypeRadioGroup.check(R.id.userRadioButton)
                        } else {
                            userTypeRadioGroup.check(R.id.guardianRadioButton)
                        }

                    } ?: run {
                        Log.e(TAG, "회원정보가 null입니다.") }
                } else {
                    Toast.makeText(this@UserInfoUpdateActivity, "회원정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<MemberViewDto>, t: Throwable) {
                Toast.makeText(this@UserInfoUpdateActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserInformation() {
        // 사용자 입력값 읽기
        val email = emailEditText.text.toString()  // email 값을 여기서 읽음
        val password = passwordEditText.text.toString()
        val name = nameEditText.text.toString()
        val phone = phoneEditText.text.toString()

        // DTO 생성
        val memberInfoUpdateDto = MemberInfoUpdateDto(
            password = password,
            userName = name,
            phoneNumber = phone
        )

        Log.d("UserInfoUpdateActivity", "Email: $email, Password: $password, UserName: $name, PhoneNumber: $phone")

        // Retrofit 인스턴스 생성 및 API 호출
        // val retrofit = RetrofitBuilder.getRetrofit()
        val service = RetrofitBuilder.createService(MemberInfoUpdate::class.java)
        val call = service.updateMemberInfo(email, memberInfoUpdateDto)

        call.enqueue(object : Callback<MemberInfoUpdateDto> {
            override fun onResponse(call: Call<MemberInfoUpdateDto>, response: Response<MemberInfoUpdateDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UserInfoUpdateActivity, "회원정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@UserInfoUpdateActivity, "회원정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberInfoUpdateDto>, t: Throwable) {
                Toast.makeText(this@UserInfoUpdateActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
