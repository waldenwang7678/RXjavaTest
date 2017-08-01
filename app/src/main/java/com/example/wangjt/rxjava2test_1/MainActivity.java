package com.example.wangjt.rxjava2test_1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements TextViewPresenter {
    @InjectView(R.id.info)
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    public void showInfo(String str) {
        info.setText(str);
    }

    @OnClick(R.id.observable)
    void observal() {
        RXUtil.observable(this);
    }

    @OnClick(R.id.interval)
    void interval() {
        RXUtil.intervalImp(this);
    }

    @OnClick(R.id.interval_cancle)
    void interval_cancle() {
        RXUtil.intervalCancle(this);
    }

    @OnClick(R.id.map)
    void map() {
        RXUtil.map(this);
    }

    @OnClick(R.id.concat)
    void concat() {
        RXUtil.concat(this);
    }

    @OnClick(R.id.flatmap)
    void flatmap() {
        RXUtil.flatmap(this);
    }

}
