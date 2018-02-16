package com.applications.dk_sky.calcapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

public class MainActivity extends AppCompatActivity {
    private static final String DIGITS = "0123456789";
    private static final String OPERATORS = "+-/x^%";
    private static final String CONSTANTS = "πeφ";


    // Declaration of shared variables, widgets and views
    private TextView txtDisplay;
    private TextView userTextName;
    private static double calcMemory = 0;
    private static String userName;
    private static int buttonsPressed;
    private static AlertDialog dialog;
    private ActionBarDrawerToggle toggle;


    // Condition flags and counters for proper behavior of operators
    // and functionality of Delete button
    private boolean lastDigit;
    private boolean lastDot;
    private boolean lastOpenBracket;
    private boolean hasDot = false;
    private int bracketsToClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Lifecycle", "Created");

        // *** Layout-dependent logic ***
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ViewPager viewPager = findViewById(R.id.viewpager);
            KeyAdapter adapter = new KeyAdapter(this, getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            TabLayout tabLayout = findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        }

        // ***Layout-independent logic***

        txtDisplay = findViewById(R.id.display_screen);
        txtDisplay.setText("0");
        lastDigit = true;
        lastDot = false;
        lastOpenBracket = false;

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = drawerLayout.findViewById(R.id.navigation_view);
        View headerLayout = navigationView.getHeaderView(0);
        userTextName = headerLayout.findViewById(R.id.nameView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void openMenuItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(MainActivity.this,AboutLayout.class));
                break;
            case R.id.history:
                startActivity(new Intent(MainActivity.this,HistoryLayout.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Lifecycle", "Resume");
        if (userName == null) {
            openDialog();
        } else {
            userTextName.setText(userName);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Lifecycle", "Pause");
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save variables on screen rotate
        outState.putString("DISPLAY", txtDisplay.getText().toString());
        outState.putInt("BRACKETS", bracketsToClose);
        outState.putBoolean("HASDOT", hasDot);
        outState.putDouble("MEMORY", calcMemory);
        outState.putString("USERNAME", userName);
        outState.putInt("BUTTONSPRESSED", buttonsPressed);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore variables on screen rotate
        txtDisplay.setText(savedInstanceState.getString("DISPLAY"));
        bracketsToClose = savedInstanceState.getInt("BRACKETS");
        hasDot = savedInstanceState.getBoolean("HASDOT");
        calcMemory = savedInstanceState.getDouble("MEMORY");
        userName = savedInstanceState.getString("USERNAME");
        buttonsPressed = savedInstanceState.getInt("BUTTONSPRESSED");
        updateFlags();
    }

    public void openDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_dialogue, null);
        final EditText editUserName = view.findViewById(R.id.edit_username);

        dialogBuilder.setPositiveButton(R.string.loginButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String insertedName = editUserName.getText().toString();
                if (!insertedName.isEmpty()) {
                    // Assign username
                    userName = insertedName;
                    userTextName.setText(userName);
                    Log.i("login", "username " + userName);
                    dialog.dismiss();
                } else {
                    // Assign default name
                    userName = getResources().getString(R.string.placeholder);
                    Log.i("login", "username " + userName);
                }
            }
        });
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
    }


    ////**************************      CALCULATOR LOGIC        ************************************

    // Pressing Digit
    public void onDigitClick(View view) {
        buttonsPressed++;
        Button button = (Button) view;
        String text = button.getText().toString();
        if (txtDisplay.getText().toString().equals("0")) {
            txtDisplay.setText(button.getText());
        } else {
            txtDisplay.append(button.getText());
        }
        lastDigit = true;
        lastOpenBracket = false;
        lastDot = false;
        if (CONSTANTS.contains(text)) {
            hasDot = true;
        }
    }

    // Pressing Operator
    public void onOperatorClick(View view) {
        buttonsPressed++;
        Button button = (Button) view;
        String operator = button.getText().toString();
        boolean isRightBracket = txtDisplay.getText().toString().endsWith(")");
        boolean isLeftBracket = txtDisplay.getText().toString().endsWith("(");
        String oldValue = txtDisplay.getText().toString();
        oldValue = oldValue.substring(0, oldValue.length() - 1);
        if (operator.equals("+") || operator.equals("-")) {
            if ((isLeftBracket || isRightBracket || lastDigit)) {
                txtDisplay.append(button.getText());
            } else {
                txtDisplay.setText(oldValue);
                txtDisplay.append(button.getText().toString());
            }
        } else {
            if ((isRightBracket || lastDigit)) {
                txtDisplay.append(button.getText());
            } else if (!lastOpenBracket) {
                txtDisplay.setText(oldValue);
                txtDisplay.append(button.getText().toString());
            }
        }
        lastDot = false;
        lastDigit = false;
        hasDot = false;
    }

    // Pressing "="
    @SuppressLint("SetTextI18n")
    public void onEqual(View view) {
        buttonsPressed++;
        if (lastDigit) {
            try {
                if (bracketsToClose > 0) {
                    while (bracketsToClose > 0) {
                        txtDisplay.append(")");
                        bracketsToClose--;
                    }
                }
                // Get input to parse into expression, format it and evaluate
                String input = txtDisplay.getText().toString();
                double result = calculateResult(input);
                if (Double.isNaN(result)) {
                    throw new ArithmeticException();
                }
                txtDisplay.setText(Double.toString(result));
                updateFlags();

                buttonsPressed = 0;
            } catch (ArithmeticException ex) {
                Toast.makeText(this, getResources().getString(R.string.arithmeticException), Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Toast.makeText(this, getResources().getString(R.string.illegalArgumentException), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Pressing "M+"
    public void memoryAdd(View view) {
        buttonsPressed++;
        if (lastDigit) {
            try {
                if (bracketsToClose > 0) {
                    while (bracketsToClose > 0) {
                        txtDisplay.append(")");
                        bracketsToClose--;
                    }
                }
                String input = txtDisplay.getText().toString();
                double result = calculateResult(input);
                if (Double.isNaN(result)) {
                    throw new ArithmeticException();
                }
                calcMemory += result;
                Toast.makeText(this, getResources().getString(R.string.addMemory), Toast.LENGTH_SHORT).show();
            } catch (ArithmeticException ex) {
                Toast.makeText(this, getResources().getString(R.string.arithmeticException), Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Toast.makeText(this, getResources().getString(R.string.illegalArgumentException), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.cannotBeSaved), Toast.LENGTH_SHORT).show();
        }
    }

    // Pressing "M-"
    public void memorySubtract(View view) {
        buttonsPressed++;
        if (lastDigit) {
            if (bracketsToClose > 0) {
                while (bracketsToClose > 0) {
                    txtDisplay.append(")");
                    bracketsToClose--;
                }
            }
            String input = txtDisplay.getText().toString();
            try {
                double result = calculateResult(input);
                if (Double.isNaN(result)) {
                    throw new ArithmeticException();
                }
                calcMemory -= result;
                Toast.makeText(this, getResources().getString(R.string.subtractMemory), Toast.LENGTH_SHORT).show();
            } catch (ArithmeticException ex) {
                Toast.makeText(this, getResources().getString(R.string.arithmeticException), Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Toast.makeText(this, getResources().getString(R.string.illegalArgumentException), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.cannotBeSaved), Toast.LENGTH_SHORT).show();
        }
    }

    // Pressing "MR"
    @SuppressLint("SetTextI18n")
    public void memoryRecall(View view) {
        buttonsPressed++;
        txtDisplay.setText(Double.toString(calcMemory));
        updateFlags();
    }

    // Pressing "MC"
    public void memoryClear(View view) {
        buttonsPressed++;
        calcMemory = 0;
        Toast.makeText(this, getResources().getString(R.string.clearMemory), Toast.LENGTH_SHORT).show();
    }

    // Expression evaluation logic
    private double calculateResult(String input) throws ArithmeticException, IllegalArgumentException {
        // Adjusting expression for evaluation
        input = input.replace("x", "*")
                .replace("³√", "cbrt")
                .replace("√", "sqrt")
                .replace("log", "log10")
                .replace("ln", "log");
        Expression expression = new ExpressionBuilder(input)
                .function(fact)
                .operator(percent)
                .build();
        return expression.evaluate();
    }

    // Pressing "DEL"
    public void onDeleteClick(View view) {
        buttonsPressed++;
        String oldValue = txtDisplay.getText().toString();
        String newValue;
        if (oldValue.length() > 1) {
            // Check when deleting parentheses
            if (oldValue.endsWith(")")) {
                bracketsToClose++;
            } else if (oldValue.endsWith("(")) {
                bracketsToClose--;
            }
            newValue = oldValue.substring(0, oldValue.length() - 1);
        } else {
            newValue = "0";
            bracketsToClose = 0;
            lastOpenBracket = false;
        }
        txtDisplay.setText(newValue);
        // Setting appropriate flags after each character deletion
        if (oldValue.endsWith(".")) {
            lastDot = false;
            hasDot = false;
        }
        updateFlags();
    }

    // Updates conditional flags based on the value of last character of the expression
    private void updateFlags() {
        // Check new last character
        String newValue = txtDisplay.getText().toString();
        String newLastChar;
        if (newValue.length() > 1) {
            newLastChar = String.valueOf(newValue.charAt(newValue.length() - 1));
        } else {
            newLastChar = String.valueOf(newValue.charAt(0));
        }
        if (DIGITS.contains(newLastChar)) {
            String[] parts = newValue.split("[+\\-%^x/()]");
            String lastNumber = parts[parts.length - 1];
            if (lastNumber.contains(".")) {
                hasDot = true;
            }
            lastDigit = true;
            lastOpenBracket = false;
        } else if (newLastChar.equals(".")) {
            lastDigit = false;
            lastDot = true;
            lastOpenBracket = false;
        } else if (OPERATORS.contains(newLastChar)) {
            lastDigit = false;
            lastDot = false;
        } else if (newLastChar.equals("(")) {
            lastOpenBracket = true;
            lastDigit = false;
        } else if (newLastChar.equals(")")) {
            lastOpenBracket = false;
            lastDigit = true;
        }
    }

    // Pressing "Clear"
    public void onClearClick(View view) {
        buttonsPressed = 0;
        txtDisplay.setText("0");
        bracketsToClose = 0;
        lastOpenBracket = false;
        lastDigit = true;
        lastDot = false;
        hasDot = false;
    }

    // Pressing "."
    public void onDotClick(View view) {
        buttonsPressed++;
        boolean isRightBracket;
        boolean isLeftBracket;
        if (txtDisplay.length() > 0) {
            isRightBracket = txtDisplay.getText().toString().endsWith(")");
            isLeftBracket = txtDisplay.getText().toString().endsWith("(");
        } else {
            isLeftBracket = false;
            isRightBracket = false;
        }
        if (!isLeftBracket && !isRightBracket && lastDigit && !lastDot && !hasDot) {
            txtDisplay.append(".");
            lastDigit = false;
            lastDot = true;
            hasDot = true;
        }
    }

    // Pressing "("
    public void onLeftBracketClick(View view) {
        buttonsPressed++;
        txtDisplay.append("(");
        lastDigit = false;
        lastOpenBracket = true;
        bracketsToClose++;
        hasDot = false;
    }

    // Pressing ")"
    public void onRightBracketClick(View view) {
        buttonsPressed++;
        if (lastDigit && bracketsToClose > 0) {
            txtDisplay.append(")");
            bracketsToClose--;
            lastOpenBracket = false;
            hasDot = false;
        }
    }

    // Pressing function buttons
    @SuppressLint("SetTextI18n")
    public void onFunctionClick(View view) {
        buttonsPressed++;
        Button button = (Button) view;
        if (txtDisplay.getText().toString().equals("0")) {
            if (button.getText().equals("|x|")) {
                txtDisplay.setText("abs");
            } else if (button.getText().equals("!")) {
                txtDisplay.setText("fact");
            } else {
                txtDisplay.setText(button.getText());
            }
        } else {
            if (button.getText().equals("|x|")) {
                txtDisplay.append("abs");
            } else if (button.getText().equals("!")) {
                txtDisplay.append("fact");
            } else {
                txtDisplay.append(button.getText());
            }
        }
        txtDisplay.append("(");
        lastOpenBracket = true;
        bracketsToClose++;
        lastDigit = false;
        lastDot = false;
        hasDot = false;
    }

    //Custom functions and operators for expression
    Function fact = new Function("fact") {
        @Override
        public double apply(double... args) {
            final int arg = (int) args[0];
            if ((double) arg != args[0]) {
                throw new IllegalArgumentException("Operand for factorial has to be an integer");
            }
            if (arg < 0) {
                throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
            }
            double result = 1;
            for (int i = 1; i <= arg; i++) {
                result *= i;
            }
            return result;
        }
    };

    Operator percent = new Operator("%", 2, true, Operator.PRECEDENCE_POWER - 1) {
        @Override
        public double apply(double... args) {
            return args[1] / 100 * args[0];
        }
    };
}
