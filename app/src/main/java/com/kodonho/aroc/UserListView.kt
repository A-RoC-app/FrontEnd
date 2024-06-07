package com.kodonho.aroc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kodonho.aroc.databinding.ActivityUserListViewBinding

class UserListView : AppCompatActivity() {
    private lateinit var binding: ActivityUserListViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 바인딩 초기화
        binding = ActivityUserListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인셋 리스너 설정
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 버튼 클릭 리스너 설정
        binding.MyInfoBtn.setOnClickListener {
            val intent = Intent(this, UserInfoUpdateActivity::class.java)
            startActivity(intent)
        }
    }
}
