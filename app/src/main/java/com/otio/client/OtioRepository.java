package com.otio.client;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class OtioRepository {
    private String baseURL = "http://10.0.2.2:6846";
    private SharedPreferences sharedPreferences;
    private SharedPreferences calendarPreferences;

    public interface TokenCallback {
        void tokenCheck(boolean isTokenValid);
    }

    public void checkToken(ExecutorService srv, Handler uiHandler, TokenCallback callback) {
        srv.submit(() -> {
           try {
               StringBuilder builder = new StringBuilder();
               builder.append(baseURL).append("/users/").append(sharedPreferences.getString("Username", ""));

               URL url = new URL(builder.toString());

               HttpURLConnection connection = (HttpURLConnection)url.openConnection();

               connection.setDoInput(true);
               connection.setRequestProperty("Content-Type", "application/JSON");
               connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

               int responseCode = connection.getResponseCode();

               if (responseCode == HttpURLConnection.HTTP_OK)  {
                   BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                   StringBuilder buffer = new StringBuilder();
                   String line = "";
                   while ((line = reader.readLine()) != null) {
                       buffer.append(line);
                   }

                   JSONObject response = new JSONObject(buffer.toString());

                   if (response.getString("status").equals("OK")) {
                       JSONObject userData = response.getJSONObject("data");

                       if (userData.getString("username").equals(sharedPreferences.getString("Username", ""))) {
                           callback.tokenCheck(true);
                       }
                       else {
                           callback.tokenCheck(false);
                       }
                   }
                   else {
                       callback.tokenCheck(false);
                   }
               }
               else {
                   callback.tokenCheck(false);
               }
           }
           catch (MalformedURLException e) {
               Log.e("DEV",e.getMessage());
           }
           catch (IOException e) {
               Log.e("DEV",e.getMessage());
           }
           catch (JSONException e) {
               Log.e("DEV",e.getMessage());
           }
        });
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setCalendarPreferences(SharedPreferences calendarPreferences) {
        this.calendarPreferences = calendarPreferences;
    }

    public void searchByFullname(ExecutorService srv, Handler uiHandler, String fullname) {
        srv.submit(() -> {
            try {
                List<Activity> foundActivities = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/activities/search/fullname?fullname=").append(fullname);

                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/JSON");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                JSONObject response = new JSONObject(buffer.toString());

                for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                    List<String> timeSlots = new ArrayList<>();
                    for (int j = 0; j < response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").length(); j++) {
                        timeSlots.add(response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").getString(j));
                    }

                    foundActivities.add(new Activity(response.getJSONArray("data").getJSONObject(i).getString("id"),
                            response.getJSONArray("data").getJSONObject(i).getString("name"),
                            response.getJSONArray("data").getJSONObject(i).getString("subcategory"),
                            response.getJSONArray("data").getJSONObject(i).getString("imageName"),
                            response.getJSONArray("data").getJSONObject(i).getDouble("rating"),
                            response.getJSONArray("data").getJSONObject(i).getString("imagePath"),
                            response.getJSONArray("data").getJSONObject(i).getString("mapsLink"),
                            timeSlots));
                }

                Message msg = new Message();
                msg.obj = foundActivities;
                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV",e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV",e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        });
    }

    public void searchByName(ExecutorService srv, Handler uiHandler, String name) {
        srv.submit(() -> {
            try {
                List<Activity> foundActivities = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/activities/search/name?name=").append(name);

                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/JSON");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                        buffer.append(line);
                }
                JSONObject response = new JSONObject(buffer.toString());
                for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                    List<String> timeSlots = new ArrayList<>();
                    for (int j = 0; j < response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").length(); j++) {
                            timeSlots.add(response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").getString(j));
                    }

                    foundActivities.add(new Activity(response.getJSONArray("data").getJSONObject(i).getString("id"),
                            response.getJSONArray("data").getJSONObject(i).getString("name"),
                            response.getJSONArray("data").getJSONObject(i).getString("subcategory"),
                            response.getJSONArray("data").getJSONObject(i).getString("imageName"),
                            response.getJSONArray("data").getJSONObject(i).getDouble("rating"),
                            response.getJSONArray("data").getJSONObject(i).getString("imagePath"),
                            response.getJSONArray("data").getJSONObject(i).getString("mapsLink"),
                            timeSlots));
                }

                Message msg = new Message();

                msg.obj = foundActivities;
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }
        });
    }

    public void searchBySubcategory(ExecutorService srv, Handler uiHandler, String subcategory) {
        srv.submit(() -> {
            try {
                List<Activity> foundActivities = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/activities/search/subcategory?subcategory=").append(subcategory);

                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/JSON");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JSONObject response = new JSONObject(buffer.toString());
                for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                    List<String> timeSlots = new ArrayList<>();
                    for (int j = 0; j < response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").length(); j++) {
                        timeSlots.add(response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").getString(j));
                    }

                    foundActivities.add(new Activity(response.getJSONArray("data").getJSONObject(i).getString("id"),
                            response.getJSONArray("data").getJSONObject(i).getString("name"),
                            response.getJSONArray("data").getJSONObject(i).getString("subcategory"),
                            response.getJSONArray("data").getJSONObject(i).getString("imageName"),
                            response.getJSONArray("data").getJSONObject(i).getDouble("rating"),
                            response.getJSONArray("data").getJSONObject(i).getString("imagePath"),
                            response.getJSONArray("data").getJSONObject(i).getString("mapsLink"),
                            timeSlots));
                }

                Message msg = new Message();
                msg.obj = foundActivities;
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }


        });
    }

    public void searchByRating(ExecutorService srv, Handler uiHandler, double rating) {
        srv.submit(() -> {
            try {
                List<Activity> foundActivities = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/activities/search/rating?rating=").append(rating);

                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/JSON");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JSONObject response = new JSONObject(buffer.toString());
                for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                    List<String> timeSlots = new ArrayList<>();
                    for (int j = 0; j < response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").length(); j++) {
                        timeSlots.add(response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").getString(j));
                    }

                    foundActivities.add(new Activity(response.getJSONArray("data").getJSONObject(i).getString("id"),
                            response.getJSONArray("data").getJSONObject(i).getString("name"),
                            response.getJSONArray("data").getJSONObject(i).getString("subcategory"),
                            response.getJSONArray("data").getJSONObject(i).getString("imageName"),
                            response.getJSONArray("data").getJSONObject(i).getDouble("rating"),
                            response.getJSONArray("data").getJSONObject(i).getString("imagePath"),
                            response.getJSONArray("data").getJSONObject(i).getString("mapsLink"),
                            timeSlots));
                }

                Message msg = new Message();
                msg.obj = foundActivities;
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }


        });
    }

    public void getSavedActivities(ExecutorService srv, ActivityViewModel viewModel){
        srv.submit(() -> {
            try {
                List<Activity> foundActivities = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/savedactivities");

                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setDoInput(true);

                connection.setRequestProperty("Content-Type", "application/JSON");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JSONObject response = new JSONObject(buffer.toString());
                for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                    List<String> timeSlots = new ArrayList<>();
                    for (int j = 0; j < response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").length(); j++) {
                        timeSlots.add(response.getJSONArray("data").getJSONObject(i).getJSONArray("timeSlots").getString(j));
                    }

                    foundActivities.add(new Activity(response.getJSONArray("data").getJSONObject(i).getString("id"),
                            response.getJSONArray("data").getJSONObject(i).getString("name"),
                            response.getJSONArray("data").getJSONObject(i).getString("subcategory"),
                            response.getJSONArray("data").getJSONObject(i).getString("imageName"),
                            response.getJSONArray("data").getJSONObject(i).getDouble("rating"),
                            response.getJSONArray("data").getJSONObject(i).getString("imagePath"),
                            response.getJSONArray("data").getJSONObject(i).getString("mapsLink"),
                            timeSlots));
                }

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                   viewModel.setActivityData(foundActivities);
                });
            }
            catch (MalformedURLException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }


        });
    }

    public void getUser(ExecutorService srv, Handler uiHandler, String username) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/users/").append(username);

                URL url = new URL(builder.toString());

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/JSON");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                JSONObject response = new JSONObject(buffer.toString());


                JSONObject userData = response.getJSONObject("data");
                List<List<String>> timeSlots = new ArrayList<List<String>>();

                if (!userData.getString("availableTimeslots").equals("null")) {
                    for (int i = 0; i < userData.getJSONArray("availableTimeslots").length(); i++) {
                        List<String> timeSlot = new ArrayList<>();
                        for (int j = 0; j < userData.getJSONArray("availableTimeslots").getJSONArray(i).length(); j++) {
                            timeSlot.add(userData.getJSONArray("availableTimeslots").getJSONArray(i).getString(j));
                        }
                        timeSlots.add(timeSlot);
                    }
                }

                List<Activity> savedActivities = new ArrayList<>();
                if (!userData.getString("savedActivities").equals("null")) {
                    for (int j = 0; j < userData.getJSONArray("savedActivities").length(); j++) {
                        List<String> activityTimeSlots = new ArrayList<>();
                        for (int k = 0; k < userData.getJSONArray("savedActivities").getJSONObject(j).getJSONArray("timeSlots").length(); k++) {
                            activityTimeSlots.add(userData.getJSONArray("savedActivities").getJSONObject(j).getJSONArray("timeSlots").getString(k));
                        }

                        savedActivities.add(new Activity(userData.getJSONArray("savedActivities").getJSONObject(j).getString("id"),
                                userData.getJSONArray("savedActivities").getJSONObject(j).getString("name"),
                                userData.getJSONArray("savedActivities").getJSONObject(j).getString("subcategory"),
                                userData.getJSONArray("savedActivities").getJSONObject(j).getString("imageName"),
                                userData.getJSONArray("savedActivities").getJSONObject(j).getDouble("rating"),
                                userData.getJSONArray("savedActivities").getJSONObject(j).getString("imagePath"),
                                userData.getJSONArray("savedActivities").getJSONObject(j).getString("mapsLink"),
                                activityTimeSlots));
                    }
                }


                User foundUser = new User(response.getJSONObject("data").getString("username"),
                    response.getJSONObject("data").getString("name"),
                    response.getJSONObject("data").getString("lastname"),
                    response.getJSONObject("data").getString("ppPath"),
                    timeSlots,
                    savedActivities);

                Message msg = new Message();
                msg.obj = foundUser;
                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }


        });
    }

    public void loginUser(ExecutorService srv, Handler uiHandler, String username, String password) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/login");
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                connection.setDoInput(true);
                connection.setDoOutput(true);

                JSONObject loginDetails = new JSONObject();
                loginDetails.put("username", username);
                loginDetails.put("password", password);

                OutputStream os = connection.getOutputStream();
                os.write(loginDetails.toString().getBytes());
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(buffer.toString());

                Message msg = new Message();

                if (response.getString("status").equals("OK")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("AuthToken", response.getJSONObject("data").getString("token"));
                    editor.putString("Username", username);
                    editor.apply();
                    msg.what = 1;

                } else {
                    msg.what = 0;
                }

                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            }
            catch (ConnectException e) {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = "Server is not working";
                uiHandler.sendMessage(msg);
            }
            catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            }
            catch (JSONException e) {
                Log.e("DEV", "JSON Exception", e);
            }

        });
    }

    public void logoutUser(ExecutorService srv, Handler uiHandler) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/logout");
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();

                Message msg = new Message();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("AuthToken");
                    editor.remove("Username");
                    editor.apply();
                    msg.what = 1;
                } else {
                    msg.what = 0;
                }
                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            } catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            }
        });
    }

    public void getTimeslots(ExecutorService srv, Handler uiHandler) {
        srv.submit(() -> {
            try {
                List<List<String>> userTimeslots = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/gettimeslots");

                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/JSON");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                JSONObject response = new JSONObject(buffer.toString());
                for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                    List<String> timeSlots = new ArrayList<>();
                    for (int j = 0; j < response.getJSONArray("data").getJSONArray(i).length(); j++) {
                        timeSlots.add(response.getJSONArray("data").getJSONArray(i).getString(j));
                    }

                    userTimeslots.add(timeSlots);
                }

                Message msg = new Message();
                msg.obj = userTimeslots;
                uiHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (IOException e) {
                Log.e("DEV", e.getMessage());
            }
            catch (JSONException e) {
                Log.e("DEV", e.getMessage());
            }
        });
    }

    public void registerUser(ExecutorService srv, Handler uiHandler, String name, String lastname, String username, String password) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/register");
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                JSONObject userDetails = new JSONObject();
                userDetails.put("name", name);
                userDetails.put("lastname", lastname);
                userDetails.put("username", username);
                userDetails.put("password", password);


                OutputStream os = connection.getOutputStream();
                os.write(userDetails.toString().getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(buffer.toString());

                Message msg = new Message();

                if (response.getString("status").equals("OK")) {
                    msg.what = 1;
                }
                else {
                    msg.what = 0;
                }

                uiHandler.sendMessage(msg);
            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            }
            catch (ConnectException e) {
                Log.e("DEV", "Connect Exception", e);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = "Server is not working";
                uiHandler.sendMessage(msg);
            }
            catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            } catch (JSONException e) {
                Log.e("DEV", "JSON Exception", e);
            }
        });
    }

    public void deleteUserAccount(ExecutorService srv, Handler uiHandler) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/deleteaccount");
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(buffer.toString());

                Message msg = new Message();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    msg.what = 1;
                } else {
                    msg.what = 0;
                }

                uiHandler.sendMessage(msg);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    SharedPreferences.Editor calendarEditor = calendarPreferences.edit();
                    editor.remove("AuthToken");
                    calendarEditor.remove(sharedPreferences.getString("Username", ""));
                    calendarEditor.apply();
                    editor.remove("Username");
                    editor.apply();
                }
            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            } catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            } catch (JSONException e) {
                Log.e("DEV", "JSON Exception", e);
            }
        });
    }

    public void addTimeslot(ExecutorService srv, Handler uiHandler, List<String> timeslot) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/addtimeslot");
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                JSONArray timesArray = new JSONArray(timeslot);

                OutputStream os = connection.getOutputStream();
                os.write(timesArray.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Message msg = new Message();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    msg.what = 1;
                } else {
                    msg.what = 0;
                }
                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            } catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            }
        });
    }


    public void removeTimeslot(ExecutorService srv, Handler uiHandler, List<String> timeslot) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/removetimeslot");
                URL url = new URL(builder.toString()); // Endpoint for deleting a timeslot
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));
                connection.setDoOutput(true);

                JSONArray timeslotArray = new JSONArray(timeslot);

                OutputStream os = connection.getOutputStream();
                os.write(timeslotArray.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();
                Message msg = new Message();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    msg.what = 1;
                } else {
                    msg.what = 0;
                }
                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            } catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            }
        });
    }

    public void saveActivity(ExecutorService srv, Handler uiHandler, Activity activity) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/saveactivity");
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));



                JSONObject activityDetails = new JSONObject();
                activityDetails.put("id", activity.getId());
                activityDetails.put("name", activity.getName());
                activityDetails.put("subcategory", activity.getSubcategory());
                activityDetails.put("imageName", activity.getImageName());
                activityDetails.put("rating", activity.getRating());
                activityDetails.put("imagePath", activity.getImagePath());
                activityDetails.put("mapsLink", activity.getMapsLink());
                JSONArray timeSlotsArray = new JSONArray(activity.getTimeSlots());
                activityDetails.put("timeSlots", timeSlotsArray);

                OutputStream os = connection.getOutputStream();
                os.write(activityDetails.toString().getBytes());
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(buffer.toString());

                Message msg = new Message();
                if (response.getString("status").equals("OK")) {
                    msg.what = 1;
                } else {
                    msg.what = 0;
                }
                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            } catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            } catch (JSONException e) {
                Log.e("DEV", "JSON Exception", e);
            }
        });
    }


    public void deleteSavedActivity(ExecutorService srv, Handler uiHandler, String activityId) {
        srv.submit(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(baseURL).append("/user/deletesavedactivity?activity_id=").append(activityId);
                URL url = new URL(builder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", sharedPreferences.getString("AuthToken", ""));

                int responseCode = connection.getResponseCode();


                BufferedReader reader;
                if (responseCode != HttpURLConnection.HTTP_NOT_FOUND) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuilder responseBuffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuffer.append(line);
                }
                reader.close();
                JSONObject response = new JSONObject(responseBuffer.toString());

                Message msg = new Message();
                if ((activityId != null) && (!sharedPreferences.getString("AuthToken", "").equals("")) && (responseCode == HttpURLConnection.HTTP_NOT_FOUND) && (response.getString("path").equals("/error/invalidrequest"))) {
                    msg.what = 1;
                } else {
                    msg.what = 0;
                }

                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                Log.e("DEV", "Malformed URL Exception", e);
            } catch (IOException e) {
                Log.e("DEV", "IO Exception", e);
            } catch (JSONException e) {
                Log.e("DEV", "JSON Exception", e);
            }
        });
    }
}
