package ac.baekseok.pitmeproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image"); //database Image폴더 생성
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Button select_btn;

    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //컴포넌트 객체에 담기
        Button btnUpload = (Button) findViewById(R.id.upload_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress_View);
        imageView = (ImageView) findViewById(R.id.image_view);
        select_btn = (Button) findViewById(R.id.select_btn);

        //프로그래스바 숨기기
        progressBar.setVisibility(View.INVISIBLE);

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

        //업로드버튼 클릭 이벤트
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //선택한 이미지가 있다면
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(ProfileActivity.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //돌아가기 버튼
        Button btnReturn = (Button) findViewById(R.id.btnReturn);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }//onCreate()

    //사진 가져오기
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
        Intent intent = getIntent(); //email 받기위한 intent
        String email = intent.getStringExtra("email");
        StorageReference fileRef = reference.child(email).child("Profile");

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //이미지 모델에 담기
                        Image_Model model = new Image_Model(uri.toString());

                        //키로 아이디 생성
                        String modelId = root.push().getKey();

                        //데이터 넣기
                        root.child(modelId).setValue(model);

                        //프로그래스바 숨김
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(ProfileActivity.this, "변경완료", Toast.LENGTH_SHORT).show();

                        imageView.setImageResource(R.drawable.ic_person);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //프로그래스바 보여주기
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    //파일타입 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
