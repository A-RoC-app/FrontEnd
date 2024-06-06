package com.kodonho.aroc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kodonho.aroc.api.MemberInfoUpdate
import com.kodonho.aroc.dto.MemberInfoUpdateDto
import com.kodonho.aroc.RetrofitInstance.RetrofitBuilder
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

        // 버튼 클릭 리스너 설정
        updateButton.setOnClickListener {
            updateUserInformation()
        }
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

        // Retrofit 인스턴스 생성 및 API 호출
        val retrofit = RetrofitBuilder.getRetrofit()
        val service = retrofit.create(MemberInfoUpdate::class.java)
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
