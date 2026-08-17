package com.vinodnarwade.eduquiz.teacheractivities;

import java.util.ArrayList;
import java.util.List;

public class QuestionBankCache {

    private static final List<QuestionBankModel> cachedQuestionBanks =
            new ArrayList<>();

    private static String cachedUserId = null;

    public static boolean hasData(String userId) {

        return cachedUserId != null
                && cachedUserId.equals(userId)
                && !cachedQuestionBanks.isEmpty();
    }

    public static List<QuestionBankModel> getData() {

        return new ArrayList<>(cachedQuestionBanks);
    }

    public static void saveData(
            String userId,
            List<QuestionBankModel> questionBanks) {

        cachedQuestionBanks.clear();

        cachedQuestionBanks.addAll(questionBanks);

        cachedUserId = userId;
    }

    public static void clear() {

        cachedQuestionBanks.clear();

        cachedUserId = null;
    }
}