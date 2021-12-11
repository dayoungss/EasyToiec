package com.example.toiquewordbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ExamFragment extends Fragment {

    QuizActivity quizActivity;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private TextView quiz_question;
    private Button bt[] = new Button[4];
    private Button btNext, btEnd;
    private int btPressed=0;
    private ProgressBar quiz_progressbar;
    private int day; // 날짜
    private int N=10; // 전체 문제 수
    private int cnt=1; // 푼 문제 수
    private int answerIndex; //정답이 있는 인덱스

    private String dayString;
    ArrayList<Word> wordArrayList = new ArrayList<>();
    DBQueryManager dayManager;

    Context mcontext;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_exam,container, false);
        quizActivity = (QuizActivity) getActivity();
        mcontext = container.getContext();

        quiz_question = (TextView) v.findViewById(R.id.quiz_question);
        bt[1] = (AppCompatButton) v.findViewById(R.id.bt1);
        bt[2] = (AppCompatButton) v.findViewById(R.id.bt2);
        bt[3] = (AppCompatButton) v.findViewById(R.id.bt3);
        btNext = (Button) v.findViewById(R.id.btNext);
        btEnd = (Button) v.findViewById(R.id.btEnd);
        quiz_progressbar = (ProgressBar) v.findViewById(R.id.quiz_progressbar);
        day = quizActivity.getDay();

        dayString = "DAY_" + day;
        dayManager = new DBQueryManager(dayString);
        wordArrayList = dayManager.getWordList(mcontext);
        makeQuestion();

        // 버튼이 하나만 선택되도록 하는 함수
        bt[1].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (btPressed==2) bt[2].setPressed(false);
                if (btPressed==3) bt[3].setPressed(false);
                btPressed=1;
                bt[1].setPressed(true);
                bt[1].setTextColor(getResources().getColor(R.color.background));
                bt[2].setTextColor(getResources().getColor(R.color.icon_color));
                bt[3].setTextColor(getResources().getColor(R.color.icon_color));
                return true;
            }
        });

        bt[2].setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (btPressed==1) bt[1].setPressed(false);
                if (btPressed==3) bt[3].setPressed(false);
                btPressed=2;
                bt[2].setPressed(true);
                bt[1].setTextColor(getResources().getColor(R.color.icon_color));
                bt[2].setTextColor(getResources().getColor(R.color.background));
                bt[3].setTextColor(getResources().getColor(R.color.icon_color));
                return true;
            }
        });

        bt[3].setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (btPressed==1) bt[1].setPressed(false);
                if (btPressed==2) bt[2].setPressed(false);
                btPressed=3;
                bt[3].setPressed(true);
                bt[1].setTextColor(getResources().getColor(R.color.icon_color));
                bt[2].setTextColor(getResources().getColor(R.color.icon_color));
                bt[3].setTextColor(getResources().getColor(R.color.background));
                return true;
            }
        });

        // next 버튼 눌렀을 때 문제 바꾸는 함수
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();

                Handler mHandler = new Handler();
                new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩
                        makeQuestion();
                        for (int i=1; i<=3; i++) {
                            bt[i].setPressed(false);
                            bt[i].setTextColor(getResources().getColor(R.color.icon_color));
                            bt[i].setBackgroundResource(R.drawable.selector_pressed);
                        }
                        btPressed=0;
                        mHandler.sendEmptyMessage(0);	// 실행이 끝난후 알림
                    }
                }, 1000);



            }
        });


        // 퀴즈 종료버튼 누르면 오답체크 페이지로 이동
        btEnd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkAnswer();
                Handler mHandler = new Handler();
                new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩
                        quizActivity.changeFragment();

                    }
                }, 1000);
            }
        });

        return v;

    }


    private int intFilter(int n) {
        if(n==4)
            return 1;
        else if (n==5)
            return 2;
        else
            return n;
    }


    //next 버튼 누르면 단어 변경하는거
    public void makeQuestion() {
        String question,answer;
        String[] choice = new String[4];
        // 문제 정답 확인하기

        cnt++;
        quiz_progressbar.setProgress(cnt*10);
        if (cnt == N) {
            btNext.setVisibility(View.GONE);
            btEnd.setVisibility(View.VISIBLE);
        }

        /********이 아래 코드 변경*************/

        // DB에서 _id순? 알파벳순 순차 단어 선택
        question=wordArrayList.get(cnt-2).getEng();
        answer=wordArrayList.get(cnt-2).getKor();
        /*********************/



        Random rand = new Random();
        answerIndex = rand.nextInt(3)+1; //1,2,3중 1개 랜덤

        choice[answerIndex]=answer;
        choice[intFilter(answerIndex+1)]="tmp";
        choice[intFilter(answerIndex+2)]="tmp";



        for(int i=1; i<=3; i++) {
            if (!choice[i].equals(answer)) {
                choice[i] = DBQueryManager.getRandomMeans(mcontext);
            }
        }

        quiz_question.setText(question);
        bt[1].setText(choice[1]);
        bt[2].setText(choice[2]);
        bt[3].setText(choice[3]);
    }

    public boolean checkAnswer (){
        bt[answerIndex].setBackgroundResource(R.drawable.selector_correct);
        bt[answerIndex].setPressed(true);
        if (btPressed==answerIndex){
            quizActivity.score++;
            return true;
        }
        else {

            if(btPressed!=0) bt[btPressed].setBackgroundResource(R.drawable.selector_wrong);
            bt[btPressed].setPressed(true);
            return false;
        }
    }
}