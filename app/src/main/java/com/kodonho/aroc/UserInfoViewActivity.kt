package com.kodonho.aroc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kodonho.aroc.RetrofitInstance.RetrofitBuilder
import com.kodonho.aroc.api.MemberView
import com.kodonho.aroc.dto.MemberViewDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoViewActivity : AppCompatActivity() {

    private lateinit var infoEmail: TextView
    private lateinit var infoName: TextView
    private lateinit var infoPhone: TextView
    private lateinit var userTypeRadioGroup: RadioGroup
    private lateinit var userRadioButton: RadioButton
    private lateinit var guardianRadioButton: RadioButton

    private lateinit var sharedPreferences: SharedPreferences
    private var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_view)
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Retrofit 초기화
        RetrofitBuilder.initialize(applicationContext)

        // 뷰 초기화
        infoEmail = findViewById(R.id.infoEmail)
        infoName = findViewById(R.id.infoName)
        infoPhone = findViewById(R.id.infoPhone)
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup_info)
        userRadioButton = findViewById(R.id.userRadioButton_info)
        guardianRadioButton = findViewById(R.id.guardianRadioButton_info)

        val editBtn : Button = findViewById(R.id.editBtn)
        val deleteBtn : Button = findViewById(R.id.deleteBtn)
        val backButton: ImageButton = findViewById(R.id.backButton)

        // SharedPreferences에서 이메일 가져오기
        email = sharedPreferences.getString("user_email", null)

        if (email != null) {
            // 회원 정보 가져오기
            fetchMemberInfo(email!!)
        } else {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            // 로그인 화면으로 이동하는 로직 추가
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
        // 수정 버튼 클릭 리스너
        editBtn.setOnClickListener {
            val intent = Intent(this, UserInfoUpdateActivity::class.java)
            startActivity(intent)
        }

        // 삭제 버튼 클릭 리스너
        deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    // 사용자 정보 받아오기
    private fun fetchMemberInfo(email: String) {
        val memberViewService = RetrofitBuilder.createService(MemberView::class.java)

        memberViewService.getMemberInfo(email).enqueue(object : Callback<MemberViewDto> {
            override fun onResponse(call: Call<MemberViewDto>, response: Response<MemberViewDto>) {
                if (response.isSuccessful) {
                    val memberViewDto = response.body()
                    Log.d(TAG, "Member Info: $memberViewDto")
                    if (memberViewDto != null) {
                        val userType = memberViewDto.userType
                        Log.d(TAG, "UserType: $userType") // userType 로그 출력
                        infoEmail.text = memberViewDto.email
                        infoName.text = memberViewDto.name
                        infoPhone.text = memberViewDto.phone

                        when (userType) {
                            0 -> { // 보호자
                                guardianRadioButton.isChecked = true
                            }
                            1 -> { // 사용자
                                userRadioButton.isChecked = true
                            }
                            else -> {
                                Toast.makeText(this@UserInfoViewActivity, "알 수 없는 사용자 유형입니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@UserInfoViewActivity, "사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserInfoViewActivity, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberViewDto>, t: Throwable) {
                Toast.makeText(this@UserInfoViewActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 회원 탈퇴 확인 다이얼로그 표시
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("회원 탈퇴")
            .setMessage("정말 회원 탈퇴를 하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                deleteMemberInfo()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 회원 탈퇴 요청
    private fun deleteMemberInfo() {
        val memberViewService = RetrofitBuilder.createService(MemberView::class.java)

        email?.let {
            memberViewService.deleteMemberInfo(it).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UserInfoViewActivity, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 액티비티 종료
                    } else {
                        Toast.makeText(this@UserInfoViewActivity, "회원 탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@UserInfoViewActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "이메일 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "UserInfoViewActivity"
    }
}
