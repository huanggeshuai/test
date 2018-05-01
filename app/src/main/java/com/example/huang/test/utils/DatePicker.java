package com.example.huang.test.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by huang on 2017/7/29.
 */

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private String date;
    int time;
    public DatePicker(int time) {
        this.time=time;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar= Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day){
            @Override
            protected void onStop() {
                super.onStop();
            }
        };

    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        if(time!=1 && time!=2){
            time=2;
        }
     switch (time){
         case 1:Timepicker timepicker=new Timepicker();             //日期+时间
             timepicker.show(getFragmentManager(),"time_picker");
             date=year+"-"+(month+1)+"-"+dayOfMonth;
             timepicker.setTime(date);
             break;
         case 2:                                                  //只有日期
             if(getActivity() instanceof DataCallBack) {
                 //将activity强转为DataCallBack
                 DataCallBack dataCallBack = (DataCallBack) getActivity();
                 date = year + "-" + (month + 1) + "-" + dayOfMonth;
                 //调用activity的getData方法将数据传回activity显示
                 dataCallBack.getData(date);
             }
                 break;
         default:
             break;

     }

    }

    public void setTime(String time){
        time=date;

    }
}
