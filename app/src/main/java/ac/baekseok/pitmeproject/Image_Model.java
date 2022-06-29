package ac.baekseok.pitmeproject;

/*프로필 사진 firebase 업데이트에 사용*/

public class Image_Model {

    private String imageUri;

    Image_Model() {

    }

    public Image_Model(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
