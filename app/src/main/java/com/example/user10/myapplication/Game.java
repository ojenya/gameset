package com.example.user10.myapplication;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Game extends AppCompatActivity {

    Button btnShow;
    int token;
    LinearLayout llMain;
    ArrayList<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();
        token = i.getIntExtra("token",-1);
        if(token == -1){
            System.out.println("Ошибка");
        }

        btnShow = (Button) findViewById(R.id.btnShow);
        llMain = (LinearLayout)findViewById(R.id.limain);


    }

    public void onClick(View v) throws InterruptedException {
        gameThread gm = new gameThread(token);
        gm.start();
        gm.join();

        LinearLayout.LayoutParams viewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        cards = gm.getList();

        Card[] cs = new Card[cards.size()];

        for (int i = 0; i < cards.size(); i++) {
            cs[i] = cards.get(i);
        }
        CardS cardS = new CardS(cs);

        for (int i=0; i<4; i++) {
            LinearLayout tableRow = new LinearLayout(this);
            tableRow.setLayoutParams(viewLayoutParams);
            tableRow.setOrientation(LinearLayout.HORIZONTAL);
            llMain.addView(tableRow);



            for (int j=0; j<3; j++) {
                CardView cardView = new CardView(this, cardS.cards[i*3+j]);
                Display display = getWindowManager().getDefaultDisplay();
                LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, display.getHeight()/4-30,1);
                cardView.setLayoutParams(imageViewLayoutParams);
                tableRow.addView(cardView);
            }
        }

    }

    class CardS {

        Card[] cards;

        public CardS(Card[] cards) {
            //this.status = status;
            this.cards = cards;
        }

    }

    class gameThread extends Thread {

        int token;
        ArrayList<Card> cardsList = new ArrayList<>();

        public gameThread (int token) {
            this.token = token;
        }

        @Override
        public void run() {

            String reg = "{\"action\" : \"fetch_cards\", \"token\" : "+ token +" }";

            try {
                URL url = new URL("http://194.176.114.21:8050");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                OutputStream out = urlConnection.getOutputStream();
                out.write(reg.getBytes());

                Scanner in = new Scanner(urlConnection.getInputStream());

                if (in.hasNext()) {

                    String response = in.nextLine();
                    JSONObject resp = new JSONObject(response);
                    JSONArray cards = resp.getJSONArray("cards");

                    for (int k = 0; k < cards.length(); k++) {
                        JSONObject c = cards.getJSONObject(k);
                        int count = c.getInt("count");
                        int color = c.getInt("color");
                        int shape = c.getInt("shape");
                        int fill = c.getInt("fill");
                        this.cardsList.add(new Card(count, color, shape, fill));
                    }
                }

                in.close();
                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public ArrayList getList() {
            return cardsList;
        }

    }

    class Card {
        int count, fill, shape, color;

        public Card(int count, int color, int shape, int fill ) {
            this.count = count;
            this.fill = fill;
            this.shape = shape;
            this.color = color;
        }

    }

    class CardView extends View {

        Game.Card card;

        public CardView(Context context) {
            super(context);
        }

        public CardView(Context context, Game.Card card) {
            super(context);
            this.card = card;
        }

        public CardView(Context context, AttributeSet attrs, Game.Card card) {
            super(context, attrs);
            this.card = card;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(4);
            p.setColor(Color.rgb(252, 239, 186));
            canvas.drawRoundRect(10,10, canvas.getWidth()-10, canvas.getHeight()-10, 5,5,p);
            if (card.color == 1) p.setColor(Color.RED);
            else if (card.color == 2) p.setColor(Color.BLUE);
            else if (card.color == 3) p.setColor(Color.GREEN);
            int r = 0;
            r = (card.fill == 1) ? 1 : ((card.fill==2) ? 2 : 3);

            p.setStyle(Paint.Style.FILL);
            for (int i=0; i<card.count; i++) {
                if (card.shape==1)
                    canvas.drawCircle(canvas.getWidth()/2,
                            (i+1)*canvas.getHeight()/(card.count+1), 10*r, p);
                else if (card.shape==2)
                    canvas.drawRect(canvas.getWidth()/2-10*r,
                            (i+1)*canvas.getHeight()/(card.count+1)-10*r, canvas.getWidth()/2+10*r,
                            (i+1)*canvas.getHeight()/(card.count+1)+10*r, p);
                else if (card.shape==3)
                    canvas.drawOval(canvas.getWidth()/2-15*r,
                            (i+1)*canvas.getHeight()/(card.count+1)-10*r, canvas.getWidth()/2+15*r,
                            (i+1)*canvas.getHeight()/(card.count+1)+10*r, p);
            }

        }


    }

}