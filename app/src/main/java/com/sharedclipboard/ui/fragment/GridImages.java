package com.sharedclipboard.ui.fragment;

import com.sharedclipboard.R;


public class GridImages
{
    public static int[] getGridById(int id) {
        switch (id) {
            case 0:
                return getGrid1();
        }

        return null;
    }

    public static int[] getGrid1() {
        int[] grid = new int[32];

        grid[0] = R.drawable.psm_talking_on_phone2;
        grid[1] = R.drawable.psm_stressed_person;
        grid[2] = R.drawable.psm_stressed_person12;
        grid[3] = R.drawable.psm_lonely;
        grid[4] = R.drawable.psm_gambling4;
        grid[5] = R.drawable.psm_clutter3;
        grid[6] = R.drawable.psm_reading_in_bed2;
        grid[7] = R.drawable.psm_stressed_person4;
        grid[8] = R.drawable.psm_lake3;
        grid[9] = R.drawable.psm_cat;
        grid[10] = R.drawable.psm_puppy3;
        grid[11] = R.drawable.psm_neutral_person2;
        grid[12] = R.drawable.psm_beach3;
        grid[13] = R.drawable.psm_peaceful_person;
        grid[14] = R.drawable.psm_alarm_clock2;
        grid[15] = R.drawable.psm_sticky_notes2;

        grid[16] = R.drawable.psm_anxious;
        grid[17] = R.drawable.psm_hiking3;
        grid[18] = R.drawable.psm_stressed_person3;
        grid[19] = R.drawable.psm_lonely2;
        grid[20] = R.drawable.psm_dog_sleeping;
        grid[21] = R.drawable.psm_running4;
        grid[22] = R.drawable.psm_alarm_clock;
        grid[23] = R.drawable.psm_headache;
        grid[24] = R.drawable.psm_baby_sleeping;
        grid[25] = R.drawable.psm_puppy;
        grid[26] = R.drawable.psm_stressed_cat;
        grid[27] = R.drawable.psm_angry_face;
        grid[28] = R.drawable.psm_bar;
        grid[29] = R.drawable.psm_running3;
        grid[30] = R.drawable.psm_neutral_child;
        grid[31] = R.drawable.psm_headache2;

        return grid;
    }
}
