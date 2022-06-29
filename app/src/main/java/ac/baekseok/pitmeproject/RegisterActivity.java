package ac.baekseok.pitmeproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd, mEtNickName; //회원가입 입력필드
    private Button mBtnRegister; //회원가입 입력
    private Button btn_back; //돌아가기 버튼
    private StorageReference reference = FirebaseStorage.getInstance().getReference(); //업로드 위한 firebase storage
    private ImageView imageView;
    private Button select_btn;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        select_btn = (Button) findViewById(R.id.select_btn);
        imageView = findViewById(R.id.image_view);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FitMe");

        mEtNickName = (EditText) findViewById(R.id.mEtNickName);
        mEtEmail = (EditText) findViewById(R.id.mEtEmail);
        mEtPwd = (EditText) findViewById(R.id.mEtPwd);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        btn_back = (Button) findViewById(R.id.btn_back);


        //이미지 클릭 이벤트
        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        //돌아가기 버튼 클릭
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //회원가입 버튼 클릭
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strNickname = mEtNickName.getText().toString();

                //선택한 이미지가 있다면
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {

                }
                // firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            //회원가입 처리
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    UserAccount account = new UserAccount();
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    account.setPassword(strPwd);
                                    account.setNickname(strNickname);


                                    //setValue : database에 데이터 삽입
                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                                    Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }//if
                                else {
                                    Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });//setOnClickListener


    }//onCreate

    //갤러리에서 사진 가져오기
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();

                        imageView.setImageURI(imageUri);
                    }
                }
            });

    //파이어베이스 이미지 업로드
    private void uploadToFirebase(Uri uri) {
        String imageName = mEtEmail.getText().toString(); //계정이름으로 이미지 저장(다른 사람과 겹치지 않기 위한 고유한 이름 필요)
        StorageReference fileRefProfile = reference.child(String.valueOf(imageName)).child("Profile");


        //이미지 업로드 성공시
        fileRefProfile.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRefProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });
            }
        });
    }
}//RegisterActivity