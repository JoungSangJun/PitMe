package ac.baekseok.pitmeproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Cloth extends AppCompatActivity {

    Button btnTop, btnBottom;
    ImageView imgTop, imgBottom;
    private StorageReference reference = FirebaseStorage.getInstance().getReference(); //업로드 위한 firebase storage


    private Uri imageUriTop;
    private Uri imageUriBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth);

        btnTop = (Button) findViewById(R.id.btnTop);
        btnBottom = (Button) findViewById(R.id.btnBottom);
        imgTop = (ImageView) findViewById(R.id.imgTop);
        imgBottom = (ImageView) findViewById(R.id.imgBottom);

        //상의버튼 눌렀을 때
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult1.launch(galleryIntent);


            }
        });

        //하의버튼 눌렀을 때
        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult2.launch(galleryIntent);
            }
        });

        //돌아가기 버튼
        Button btnReturn = (Button) findViewById(R.id.btnReturn);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //선택한 상의이미지가 있다면
                if (imageUriTop != null) {
                    uploadToFirebaseTop(imageUriTop);
                } else {

                }
                //선택한 하의이미지가 있다면
                if (imageUriBottom != null) {
                    uploadToFirebaseBottom(imageUriBottom);
                } else {

                }
                finish();
            }
        });
    }//onCreate

    //상의사진 가져오기
    ActivityResultLauncher<Intent> activityResult1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUriTop = result.getData().getData();

                        imgTop.setImageURI(imageUriTop);
                    }
                }
            });

    //하의사진 가져오기
    ActivityResultLauncher<Intent> activityResult2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUriBottom = result.getData().getData();

                        imgBottom.setImageURI(imageUriBottom);
                    }
                }
            });

    //파이어베이스 상의이미지 업로드
    private void uploadToFirebaseTop(Uri uri) {
        Intent intent = getIntent(); //email 받기위한 intent
        String email = intent.getStringExtra("email");

        StorageReference fileRefTop = reference.child(email).child("Top");


        //이미지 업로드 성공시
        fileRefTop.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRefTop.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });
            }
        });
    }

    //파이어베이스 하의이미지 업로드
    private void uploadToFirebaseBottom(Uri uri) {
        Intent intent = getIntent(); //email 받기위한 intent
        String email = intent.getStringExtra("email");

        StorageReference fileRefTop = reference.child(email).child("Bottom");


        //이미지 업로드 성공시
        fileRefTop.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRefTop.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                });
            }
        });
    }
}