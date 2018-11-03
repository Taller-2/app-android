package ar.uba.fi.mercadolibre.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.Article;

public class ShowQrDialog extends DialogFragment {
    private Article article = null;
    private static final String ARTICLE_KEY = "article";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable(ARTICLE_KEY);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.qr_code);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.show_qr, null);
        fillImage((ImageView) layout.findViewById(R.id.qrImageView));

        builder.setView(layout);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void fillImage(ImageView imageView) {

        QRCodeWriter writer = new QRCodeWriter();
        try {
            String content = article.getID();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            imageView.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public static ShowQrDialog newInstance(Article article) {
        Bundle args = new Bundle();
        args.putSerializable(ARTICLE_KEY, article);
        ShowQrDialog fragment = new ShowQrDialog();
        fragment.setArguments(args);
        return fragment;
    }
}
