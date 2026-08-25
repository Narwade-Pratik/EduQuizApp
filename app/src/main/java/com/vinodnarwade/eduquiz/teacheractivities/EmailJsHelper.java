//package com.vinodnarwade.eduquiz.teacheractivities;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import org.json.JSONObject;
//
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Map;
//
//public class EmailJsHelper {
//
//    public interface EmailCallback {
//        void onResult(boolean success, String message);
//    }
//
//    public static void sendEmail(
//            Map<String, String> templateParams,
//            EmailCallback callback) {
//
//        new Thread(() -> {
//
//            boolean success = false;
//            String resultMessage;
//
//            try {
//
//                JSONObject paramsJson = new JSONObject();
//
//                for (Map.Entry<String, String> entry : templateParams.entrySet()) {
//                    paramsJson.put(entry.getKey(), entry.getValue());
//                }
//
//                JSONObject body = new JSONObject();
//                body.put("service_id", EmailJsConfig.SERVICE_ID);
//                body.put("template_id", EmailJsConfig.TEMPLATE_ID);
//                body.put("user_id", EmailJsConfig.PUBLIC_KEY);
//                body.put("template_params", paramsJson);
//
//                URL url = new URL("https://api.emailjs.com/api/v1.0/email/send");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/json");
//                conn.setDoOutput(true);
//
//                try (OutputStream os = conn.getOutputStream()) {
//                    os.write(body.toString().getBytes("UTF-8"));
//                }
//
//                int responseCode = conn.getResponseCode();
//
//                success = (responseCode == 200);
//                resultMessage = "HTTP " + responseCode;
//
//                conn.disconnect();
//
//            } catch (Exception e) {
//
//                resultMessage = e.getMessage();
//            }
//
//            final boolean finalSuccess = success;
//            final String finalMessage = resultMessage;
//
//            new Handler(Looper.getMainLooper()).post(() ->
//                    callback.onResult(finalSuccess, finalMessage)
//            );
//
//        }).start();
//    }
//}
package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EmailJsHelper {

    public interface EmailCallback {
        void onResult(boolean success, String message);
    }

    public static void sendEmail(
            Map<String, String> templateParams,
            EmailCallback callback) {

        new Thread(() -> {

            boolean success = false;
            String resultMessage = "";

            HttpURLConnection conn = null;

            try {

                // =========================
                // TEMPLATE PARAMETERS
                // =========================

                JSONObject paramsJson = new JSONObject();

                for (Map.Entry<String, String> entry : templateParams.entrySet()) {
                    paramsJson.put(entry.getKey(), entry.getValue());
                }


                // =========================
                // EMAILJS REQUEST BODY
                // =========================

                JSONObject body = new JSONObject();

                body.put(
                        "service_id",
                        EmailJsConfig.SERVICE_ID
                );

                body.put(
                        "template_id",
                        EmailJsConfig.TEMPLATE_ID
                );

                body.put(
                        "user_id",
                        EmailJsConfig.PUBLIC_KEY
                );

                body.put(
                        "template_params",
                        paramsJson
                );


                // =========================
                // CONNECTION
                // =========================

                URL url = new URL(
                        "https://api.emailjs.com/api/v1.0/email/send"
                );

                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");

                conn.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                conn.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                conn.setDoOutput(true);

                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);


                // =========================
                // SEND REQUEST
                // =========================

                byte[] requestData =
                        body.toString()
                                .getBytes(StandardCharsets.UTF_8);

                try (OutputStream os = conn.getOutputStream()) {

                    os.write(requestData);
                    os.flush();
                }


                // =========================
                // RESPONSE
                // =========================

                int responseCode =
                        conn.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {

                    inputStream = conn.getInputStream();

                } else {

                    inputStream = conn.getErrorStream();
                }


                // =========================
                // READ RESPONSE BODY
                // =========================

                StringBuilder responseBuilder =
                        new StringBuilder();

                if (inputStream != null) {

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(inputStream)
                            );

                    String line;

                    while ((line = reader.readLine()) != null) {

                        responseBuilder
                                .append(line)
                                .append("\n");
                    }

                    reader.close();
                }

                String responseBody =
                        responseBuilder
                                .toString()
                                .trim();


                // =========================
                // RESULT
                // =========================

                if (responseCode == 200) {

                    success = true;

                    resultMessage =
                            "Email sent successfully.";

                } else {

                    success = false;

                    resultMessage =
                            "HTTP " + responseCode;

                    if (!responseBody.isEmpty()) {

                        resultMessage +=
                                " - " + responseBody;
                    }
                }


            } catch (Exception e) {

                success = false;

                resultMessage =
                        e.getClass().getSimpleName()
                                + ": "
                                + e.getMessage();

            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
            }


            // =========================
            // RETURN RESULT TO UI
            // =========================

            final boolean finalSuccess = success;
            final String finalMessage = resultMessage;

            new Handler(
                    Looper.getMainLooper()
            ).post(() ->
                    callback.onResult(
                            finalSuccess,
                            finalMessage
                    )
            );

        }).start();
    }
}