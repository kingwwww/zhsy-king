package com.example.lenovo.eatapplication.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;


public class CodeUtils {

    private static final char[] CHARS = {
            '1','2','3','4','5','6','7','8','9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final int DEFAULT_CODE_LENGTH = 4;
    private static final int DEFAULT_LINE_NUMBER = 3;
    private static final int DEFAULT_FONT_SIZE = 60;
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 100;
    private static final int BASE_PADDING_LEFT = 30;
    private static final int BASE_PADDING_TOP = 70;
    private static final int RANGE_LEFT = 20;
    private static final int RANGE_TOP = 15;
    private static final int DEFAULT_COLOR= Color.rgb(0xee,0xee,0xee);

    private int mPaddingLeft, mPaddingTop;

    private static CodeUtils sMCodeUtils;
    private StringBuilder mStringBuilder = new StringBuilder();
    private Random mRandom = new Random();
    private  String code;

    public static CodeUtils newInstance(){
        if (sMCodeUtils == null){
            sMCodeUtils = new CodeUtils();
        }
        return sMCodeUtils;
    }

    public String getCode(){
        return code;
    }

    public Bitmap createBitmap(){
        mPaddingLeft = 0;
        mPaddingTop = 0;

        code = createCode();
        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(DEFAULT_COLOR);
        Paint paint = new Paint();
        paint.setTextSize(DEFAULT_FONT_SIZE);

        for (int i = 0; i < code.length(); i++){
            randomTextStyle(paint);
            randomPadding();
            canvas.drawText(code.charAt(i)+"", mPaddingLeft, mPaddingTop, paint);
        }

        for (int i = 0; i < DEFAULT_LINE_NUMBER; i++){
            drawLine(canvas, paint);
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    private void randomTextStyle(Paint paint){
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(mRandom.nextBoolean());
        float skewx = mRandom.nextInt(11) / 10;
        skewx = mRandom.nextBoolean() ? skewx : -skewx;
        paint.setTextSkewX(skewx);
        paint.setUnderlineText(mRandom.nextBoolean());
        paint.setStrikeThruText(mRandom.nextBoolean());
    }

    private void randomPadding(){
        mPaddingLeft += BASE_PADDING_LEFT + mRandom.nextInt(RANGE_LEFT);
        mPaddingTop = BASE_PADDING_TOP + mRandom.nextInt(RANGE_TOP);
    }

    private int randomColor(){
        mStringBuilder.setLength(0);
        String hexString;
        for (int i = 0; i < 3; i++ ){
            hexString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (hexString.length() == 1){
                hexString = "0" + hexString;
            }
            mStringBuilder.append(hexString);
        }
        return Color.parseColor("#" + mStringBuilder.toString());
    }

    private void drawLine(Canvas canvas , Paint paint){
        int color = randomColor();
        float startX = mRandom.nextInt(DEFAULT_WIDTH);
        float stopX = mRandom.nextInt(DEFAULT_WIDTH);
        float startY = mRandom.nextInt(DEFAULT_HEIGHT);
        float stopY = mRandom.nextInt(DEFAULT_HEIGHT);
        paint.setColor(color);
        paint.setStrokeWidth(1);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private String createCode(){
        mStringBuilder.setLength(0);
        for (int i = 0; i < DEFAULT_CODE_LENGTH; i++){
            mStringBuilder.append(CHARS[mRandom.nextInt(CHARS.length)]);
        }
        return mStringBuilder.toString();
    }

}
