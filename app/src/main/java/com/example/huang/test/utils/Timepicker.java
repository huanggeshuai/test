package com.example.huang.test.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;


import java.util.Calendar;

/**
 * Created by huang on 2017/7/29.
 */

public class Timepicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private String time="";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar= Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),this,hour,minute,true){
            @Override
            protected void onStop() {
                super.onStop();
            }
        };
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(getActivity() instanceof DataCallBack){
            //将activity强转为DataCallBack
            DataCallBack dataCallBack = (DataCallBack) getActivity();
            time = time +" "+ hourOfDay + ":" + minute +":00";
            //调用activity的getData方法将数据传回activity显示
            dataCallBack.getData(time);
        }


    }
    public void setTime(String date){
        time+=date;

    }
}
