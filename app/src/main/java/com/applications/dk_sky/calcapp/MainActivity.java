package com.applications.dk_sky.calcapp;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

import java.lang.reflect.InvocationTargetException;
import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String DIGITS = "0123456789";
    private static final String OPERATORS = "+-/x^%";
    private static final String CONSTANTS = "πeφ";

    private TextView txtDisplay;
    double calcMemory = 0;

    // Condition flags and counters for proper behavior
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save variables on screen rotate
        outState.putString("DISPLAY", txtDisplay.getText().toString());
        outState.putInt("BRACKETS", bracketsToClose);
        outState.putBoolean("HASDOT",hasDot);
        outState.putDouble("MEMORY",calcMemory);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore variables on screen rotate
        txtDisplay.setText(savedInstanceState.getString("DISPLAY"));
        bracketsToClose = savedInstanceState.getInt("BRACKETS");
        hasDot = savedInstanceState.getBoolean("HASDOT");
        calcMemory = savedInstanceState.getDouble("MEMORY");
        updateFlags();
    }


    // Pressing Digit
    public void onDigitClick(View view) {
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
            hasDot=true;
        }
    }

    // Pressing Operator
    public void onOperatorClick(View view) {
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
        hasDot=false;
    }

    // Pressing "="
    @SuppressLint("SetTextI18n")
    public void onEqual(View view) {
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
                txtDisplay.setText(Double.toString(result));
                updateFlags();
            } catch (ArithmeticException ex) {
                Toast.makeText(this, "This operation is not allowed", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Toast.makeText(this, "Something wrong with your expression. Check for mistakes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Pressing "M+"
    public void memoryAdd(View view) {
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
                calcMemory += result;
                Toast.makeText(this, "Result saved to memory", Toast.LENGTH_SHORT).show();
            } catch (ArithmeticException ex) {
                Toast.makeText(this, "This operation is not allowed", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Toast.makeText(this, "Something wrong with your expression. Check for mistakes", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Cannot be saved while expression isn't finished", Toast.LENGTH_SHORT).show();
        }
    }

    // Pressing "M-"
    public void memorySubtract(View view) {
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
                calcMemory -= result;
                Toast.makeText(this, "Result subtracted from memory", Toast.LENGTH_SHORT).show();
            } catch (ArithmeticException ex) {
                Toast.makeText(this, "This operation is not allowed", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Toast.makeText(this, "Something wrong with your expression. Check for mistakes", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Cannot be saved while expression isn't finished", Toast.LENGTH_SHORT).show();
        }
    }

    // Pressing "MR"
    @SuppressLint("SetTextI18n")
    public void memoryRecall(View view) {
        txtDisplay.setText(Double.toString(calcMemory));
        updateFlags();
    }

    // Pressing "MC"
    public void memoryClear(View view) {
        calcMemory = 0;
        Toast.makeText(this, "Memory cleared", Toast.LENGTH_SHORT).show();
    }

    // Expression evaluation logic
    private double calculateResult(String input) throws ArithmeticException,IllegalArgumentException {
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
        txtDisplay.setText("0");
        bracketsToClose = 0;
        lastOpenBracket = false;
        lastDigit = true;
        lastDot = false;
        hasDot = false;
    }

    // Pressing "."
    public void onDotClick(View view) {
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
        txtDisplay.append("(");
        lastDigit = false;
        lastOpenBracket = true;
        bracketsToClose++;
        hasDot=false;
    }

    // Pressing ")"
    public void onRightBracketClick(View view) {
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
        if (hasDot) {
            hasDot = false;
        }
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
