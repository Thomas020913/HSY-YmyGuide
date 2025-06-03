package com.thomas.page.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.thomas.page.R;
import com.thomas.page.admin.user.UserAddActivity;
import com.thomas.page.admin.user.UserDeleteActivity;
import com.thomas.page.admin.user.UserUpdateActivity;
import com.thomas.page.admin.user.UserQueryActivity;

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user);

        Button btnAdd = findViewById(R.id.btn_add_user);
        Button btnDelete = findViewById(R.id.btn_delete_user);
        Button btnUpdate = findViewById(R.id.btn_update_user);
        Button btnQuery = findViewById(R.id.btn_query_user);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, UserAddActivity.class)));
        btnDelete.setOnClickListener(v -> startActivity(new Intent(this, UserDeleteActivity.class)));
        btnUpdate.setOnClickListener(v -> startActivity(new Intent(this, UserUpdateActivity.class)));
        btnQuery.setOnClickListener(v -> startActivity(new Intent(this, UserQueryActivity.class)));
    }
}
