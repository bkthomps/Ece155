package ca.uwaterloo.lab4_201_04;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An individual block with specified attributes.
 */
class Block {

    private static final int BASE_PIXEL = -80;
    private static final int Y_TEXT_BASE = 160;
    private static final int X_TEXT_BASE = 120;

    private final Lab4_201_04 instance;
    private final RelativeLayout layout;
    private final ImageView block;
    private final int sizeOfBlock;
    private final Block[][] logicalGrid;

    private int value;
    private TextView valueText;

    private int xOld;
    private int yOld;
    private boolean isInMotion;

    Block(Lab4_201_04 instance, RelativeLayout layout, int blocksPerScreen, int gameBoardDimension,
          Block[][] logicalGrid, int value, int xIndex, int yIndex) {
        this.instance = instance;
        this.layout = layout;
        this.logicalGrid = logicalGrid;
        initializeValue(value);
        xOld = xIndex;
        yOld = yIndex;
        logicalGrid[yIndex][xIndex] = this;
        final int BLOCK_PIXEL_SIZE = 130;
        sizeOfBlock = gameBoardDimension / blocksPerScreen;
        final float ratio = (float) sizeOfBlock / (blocksPerScreen * BLOCK_PIXEL_SIZE);
        block = createImageViewProperties();
        block.setImageResource(R.drawable.gameblock);
        block.setScaleX(ratio);
        block.setScaleY(ratio);
        setToIndex(xIndex, yIndex);
        valueText.bringToFront();
    }

    void mergeIntoSelf() {
        setValue(value * 2);
        if (value == 256) {
            instance.gameWin();
        }
    }

    private void initializeValue(int value) {
        this.value = value;
        final String valueString = String.format(Locale.US, "%04d", value);
        valueText = new TextView(instance.getApplicationContext());
        valueText.setText(valueString);
        layout.addView(valueText);
        final int textSize = 30;
        valueText.setTextSize(textSize);
        valueText.setTextColor(Color.BLACK);
    }

    private void setValue(int value) {
        this.value = value;
        final String valueString = String.format(Locale.US, "%04d", value);
        valueText.setText(valueString);
    }

    /**
     * Creates an image handle and adds it to the view.
     *
     * @return image view handle
     */
    private ImageView createImageViewProperties() {
        final ImageView handle = new ImageView(instance.getApplicationContext());
        layout.addView(handle);
        return handle;
    }

    /**
     * Sets the block to the specified grid indices.
     *
     * @param xIndex x index
     * @param yIndex y index
     */
    private void setToIndex(int xIndex, int yIndex) {
        final int xPixel = BASE_PIXEL + sizeOfBlock * xIndex;
        final int yPixel = BASE_PIXEL + sizeOfBlock * yIndex;
        block.setX(xPixel);
        block.setY(yPixel);
        valueText.setX(xPixel + X_TEXT_BASE);
        valueText.setY(yPixel + Y_TEXT_BASE);
        setLogicalGrid(xIndex, yIndex);
    }

    /**
     * Moves the block to the specified grid indices.
     *
     * @param xIndex x index
     * @param yIndex y index
     */
    void moveToIndex(int xIndex, int yIndex) {
        // TODO: these two checks should be gone in lab 4
        if (xIndex == -1) {
            xIndex = xOld;
        }
        if (yIndex == -1) {
            yIndex = yOld;
        }
        animateBlockMove(xIndex, yIndex);
        setLogicalGrid(xIndex, yIndex);
    }

    /**
     * Sets the logical grid at the specified index to the current block.
     *
     * @param xIndex x index
     * @param yIndex y index
     */
    private void setLogicalGrid(int xIndex, int yIndex) {
        logicalGrid[yOld][xOld] = null;
        xOld = xIndex;
        yOld = yIndex;
        logicalGrid[yIndex][xIndex] = this;
    }

    /**
     * Animates the motion of the block.
     *
     * @param xIndex x index
     * @param yIndex y index
     */
    private void animateBlockMove(final int xIndex, final int yIndex) {
        final int EXECUTE_PERIOD_IN_MILLI_SECONDS = 1;
        final int PIXEL_CHANGE_ON_ITERATION = 5;
        final boolean isPositiveX = xIndex > xOld;
        final boolean isPositiveY = yIndex > yOld;

        isInMotion = true;

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int xCurrent = BASE_PIXEL + sizeOfBlock * xOld;
            final int xEnd = BASE_PIXEL + sizeOfBlock * xIndex;
            int yCurrent = BASE_PIXEL + sizeOfBlock * yOld;
            final int yEnd = BASE_PIXEL + sizeOfBlock * yIndex;

            @Override
            public void run() {
                if (xCurrent == xEnd && yCurrent == yEnd) {
                    isInMotion = false;
                    timer.cancel();
                    return;
                }
                if (xCurrent != xEnd) {
                    xCurrent += (isPositiveX ? 1 : -1) * PIXEL_CHANGE_ON_ITERATION;
                    block.setX(xCurrent);
                    valueText.setX(xCurrent + X_TEXT_BASE);
                }
                if (yCurrent != yEnd) {
                    yCurrent += (isPositiveY ? 1 : -1) * PIXEL_CHANGE_ON_ITERATION;
                    block.setY(yCurrent);
                    valueText.setY(yCurrent + Y_TEXT_BASE);
                }
            }
        }, EXECUTE_PERIOD_IN_MILLI_SECONDS, EXECUTE_PERIOD_IN_MILLI_SECONDS);
    }

    boolean isInMotion() {
        return isInMotion;
    }

    int getValue() {
        return value;
    }
}