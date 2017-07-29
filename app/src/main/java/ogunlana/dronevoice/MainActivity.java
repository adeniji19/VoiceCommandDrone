package ogunlana.dronevoice;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    private TextView text;
    private TextView text2;
    private TextToSpeech toSpeech;
    private boolean initialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView)findViewById(R.id.textView);
        text2 = (TextView)findViewById(R.id.textView2);
        initialized = false;
        toSpeech = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            int result = toSpeech.setLanguage(Locale.US);
            if (!(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED))
            {
                initialized=true;
            }
        }
    }

    public void onButtonClick(View view)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, 10);
        }
        else
        {
            Toast.makeText(this, "Device speech input not supported", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK && data != null)
        {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            takeAction(result);
            text.setText(result.toString());
        }
    }

    private void takeAction(ArrayList<String> speech)
    {
        String nextString;
        boolean start, stop, motor, up1, up2, down, forward, back;
        StringTokenizer input;
        for (String x: speech)
        {
            start = false;
            stop = false;
            motor = false;
            up1 = false;
            up2 = false;
            down = false;
            input = new StringTokenizer(x);

            while (input.hasMoreTokens())
            {
                nextString = input.nextToken();
                if (nextString.equalsIgnoreCase("start") || nextString.equalsIgnoreCase("activate"))
                {
                    start = true;
                }
                else if (nextString.equalsIgnoreCase("stop") || nextString.equalsIgnoreCase("deactivate"))
                {
                    stop = true;
                }
                else if (nextString.equalsIgnoreCase("increase"))
                {
                    up1 = true;
                }
                else if (nextString.equalsIgnoreCase("height") || nextString.equalsIgnoreCase("elevation") || nextString.equalsIgnoreCase("altitude"))
                {
                    up2 = true;
                }
                else if (nextString.equalsIgnoreCase("decrease"))
                {
                    down = true;
                }
                else if (nextString.equalsIgnoreCase("motor") || nextString.equalsIgnoreCase("motors"))
                {
                    motor = true;
                }


                if (start && motor)
                {
                    text2.setText("ACTIVATING MOTORS");
                    if (initialized)
                        toSpeech.speak("ACTIVATING MOTORS", toSpeech.QUEUE_ADD, null);
                    return;
                }
                else if (stop && motor)
                {

                    text2.setText("DEACTIVATING MOTORS");
                    if (initialized)
                        toSpeech.speak("DEACTIVATING MOTORS", toSpeech.QUEUE_ADD, null);
                    return;
                }
                else if (up1 && up2)
                {

                    text2.setText("INCREASING ALTITUDE");
                    if (initialized)
                        toSpeech.speak("INCREASING ALTITUDE", toSpeech.QUEUE_ADD, null);
                    return;
                }
                else if (down && up2)
                {

                    text2.setText("DECREASING ALTITUDE");
                    if (initialized)
                        toSpeech.speak("DECREASING ALTITUDE", toSpeech.QUEUE_ADD, null);
                    return;
                }
            }
        }
        text2.setText("COMMAND UNSUCCESSFUL");
        if (initialized)
            toSpeech.speak("COMMAND UNSUCCESSFUL", toSpeech.QUEUE_ADD, null);

    }
}
