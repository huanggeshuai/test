package com.example.huang.test.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.huang.test.R;

/**
 * Created by huang on 2017/8/9.
 */
public class AddAndRemoveAndNum extends RelativeLayout {
    private Button add,remove;
    private TextView num;
    private Addlistener maddlistener;
    public AddAndRemoveAndNum(Context context) {
        super(context);
    }
    public AddAndRemoveAndNum(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.add_remove_num,this);
        add=(Button) findViewById(R.id.add);
        remove=(Button) findViewById(R.id.remove);
        num=(TextView)findViewById(R.id.num);
        add.setOnClickListener(new myclick());
        remove.setOnClickListener(new myclick());
        if(Integer.parseInt(num.getText().toString())==0){
            remove.setEnabled(false);
            remove.setBackgroundColor(Color.GRAY);
        }
        num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
               if(Integer.parseInt(s.toString())<=0){
                   remove.setEnabled(false);
                   remove.setBackgroundColor(Color.GRAY);
               }
               else {
                   remove.setEnabled(true);
                   remove.setBackgroundResource(R.drawable.ic_remove_black_24dp);
               }
               if(maddlistener!=null){
                   maddlistener.Changelistener(Integer.parseInt(s.toString()));
               }
            }
        });
    }
    public AddAndRemoveAndNum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AddAndRemoveAndNum(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    class myclick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add:int number= Integer.parseInt(num.getText().toString());
                    number=number+1;
                    num.setText(String.valueOf(number));
                    break;
                case R.id.remove:int number1= Integer.parseInt(num.getText().toString());
                    number1=number1-1;
                    num.setText(String.valueOf(number1));
                    break;
                default:
                    break;

            }
        }
    }
    public void SetaddButtonC(boolean b,int id){
        add.setEnabled(b);
        add.setBackgroundColor(id);
    }
    public void SetaddButton(boolean b,int id){
        add.setEnabled(b);
        add.setBackgroundResource(id);
    }
    public void listener(Addlistener addlistener){
        maddlistener=addlistener;
    }
    public String getnum(){
        return num.getText().toString();
    }
    public static interface Addlistener{
      public void Changelistener(int num);
  }
  public void setnum(String num1){
      num.setText(num1);
  }
}
