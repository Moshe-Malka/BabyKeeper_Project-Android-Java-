package com.example.myapplication;

/**
 * Created by Moshe Malka on 08/05/2016.
 *
 * this enum is used by the TextView that display's the current temperature.
 * it allows for a representation of the colors Hex value by a easily-read String.
 */
public enum TemperatureColors {
    COLD {
        public String toString() {
            return "#a8dff4";
        }
    },
    NORMAL {
        public String toString() {
            return "#00ff00";
        }
    },
    WARM {
        public String toString() {
            return "#f9d502";
        }
    },
    HOT {
        public String toString() {
            return "#ff6269";
        }
    },
    TOO_HOT {
        public String toString() {
            return "#ee0000";
        }
    }

}
