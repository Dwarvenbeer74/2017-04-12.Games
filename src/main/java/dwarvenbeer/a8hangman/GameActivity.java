package dwarvenbeer.a8hangman;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private String[] words;
    private Random random;
    private String currWord;
    private LinearLayout wordLayout;
    private TextView[] charViews;
    private GridView letters;
    private LetterAdapter ltrAdapt;

    // ImageView для частей тела
    private ImageView[] bodyParts;
    // сколько всего частей тела
    private int numParts = 6;
    // счетчик попыток
    private int currPart;
    // число символов в текущем слове
    private int numChars;
    // число угаданных вариантов
    private int numCorr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        Resources res = getResources();
        words = res.getStringArray(R.array.words);
        random = new Random();
        currWord = "";
        wordLayout = (LinearLayout) findViewById(R.id.word);
        letters = (GridView) findViewById(R.id.letters);

        bodyParts = new ImageView[numParts];
        bodyParts[0] = (ImageView)findViewById(R.id.head);
        bodyParts[1] = (ImageView)findViewById(R.id.body);
        bodyParts[2] = (ImageView)findViewById(R.id.arm1);
        bodyParts[3] = (ImageView)findViewById(R.id.arm2);
        bodyParts[4] = (ImageView)findViewById(R.id.leg1);
        bodyParts[5] = (ImageView)findViewById(R.id.leg2);

        playGame();
    }

    public void playGame() {
        //play a new game
        // Выбираем случайным образом новое слово
        String newWord = words[random.nextInt(words.length)];
        // Если слово совпадает с текущим, то повторяем еще раз, пока не выберем новое слово
        while (newWord.equals(currWord)) newWord = words[random.nextInt(words.length)];
        currWord = newWord; // выбрали
        // программно создадим столько TextView, сколько символов в слове
        charViews = new TextView[currWord.length()];
        // но сначала удалим все элементы от прошлой игры
        wordLayout.removeAllViews();

        // создаём массив новых TextView
        for (int c = 0; c < currWord.length(); c++) {
            charViews[c] = new TextView(this);
            charViews[c].setText("" + currWord.charAt(c));

            charViews[c].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            charViews[c].setGravity(Gravity.CENTER);
            charViews[c].setTextColor(Color.WHITE);
            charViews[c].setBackgroundResource(R.drawable.letter_bg);
            // добавляем в разметку
            wordLayout.addView(charViews[c]);
        }

        ltrAdapt = new LetterAdapter(this);
        letters.setAdapter(ltrAdapt);
        currPart = 0;
        numChars = currWord.length();
        numCorr = 0;

        for (int p = 0; p < numParts; p++) {
            bodyParts[p].setVisibility(View.INVISIBLE);
        }
    }

    public void letterPressed(View view) {
        //user has pressed a letter to guess
        String ltr = ((Button) view).getText().toString();
        char letterChar = ltr.charAt(0);
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.letter_down);
        boolean correct = false;

        for (int k = 0; k < currWord.length(); k++) {
            if (currWord.charAt(k) == letterChar) {
                correct = true;
                numCorr++;
                charViews[k].setTextColor(Color.BLACK);
            }
        }

        if (correct) {
            // удачная попытка
            if (numCorr == numChars) {
                // блокируем кнопки
                disableBtns();

                // выводим диалоговое окно
                AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
                winBuild.setTitle("Поздравляем");
                winBuild.setMessage("Вы победили!\n\nОтвет:\n\n" + currWord);
                winBuild.setPositiveButton("Сыграем ещё?",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GameActivity.this.playGame();
                            }
                        }
                );

                winBuild.setNegativeButton("Выход",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GameActivity.this.finish();
                            }
                        }
                );

                winBuild.show();
            }
        } else if (currPart < numParts) {
            //some guesses left
            bodyParts[currPart].setVisibility(View.VISIBLE);
            currPart++;
        }else{
            //user has lost
            disableBtns();

            // Display Alert Dialog
            AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
            loseBuild.setTitle("Увы");
            loseBuild.setMessage("Вы проиграли!\n\nБыло загадано:\n\n"+currWord);
            loseBuild.setPositiveButton("Сыграем ещё?",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GameActivity.this.playGame();
                        }});

            loseBuild.setNegativeButton("Выход",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GameActivity.this.finish();
                        }});

            loseBuild.show();
        }
    }

    public void disableBtns() {
        int numLetters = letters.getChildCount();
        for (int l = 0; l < numLetters; l++) {
            letters.getChildAt(l).setEnabled(false);
        }
    }
}
