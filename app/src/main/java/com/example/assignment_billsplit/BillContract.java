package com.example.assignment_billsplit;

import android.provider.BaseColumns;

public final class BillContract {

    private BillContract() {
    }

    public static class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "bill_history";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DETAIL = "detail";
    }
}
