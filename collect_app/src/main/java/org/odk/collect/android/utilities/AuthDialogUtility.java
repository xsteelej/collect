/*
 * Copyright 2016 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;

/**
 * Used to present auth dialog and update credentials in the system as needed.
 */
public class AuthDialogUtility {
    private static final String TAG = "AuthDialogUtility";

    private EditText username;
    private EditText password;

    private WebCredentialsUtils webCredentialsUtils;

    public AlertDialog createDialog(final Context context,
                                    final AuthDialogUtilityResultListener resultListener, String url) {

        webCredentialsUtils = WebCredentialsUtils.getInstance();

        final View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.server_auth_dialog, null);

        String overriddenUrl = null;
        if (url != null) {
            if (!url.startsWith(webCredentialsUtils.getServerFromPreferences())) {
                overriddenUrl = url;
                if (overriddenUrl.contains("?deviceID=")) {
                    overriddenUrl = overriddenUrl.substring(0, overriddenUrl.indexOf("?deviceID="));
                }
            }
        }

        username = dialogView.findViewById(R.id.username_edit);
        password = dialogView.findViewById(R.id.password_edit);

        username.setText(overriddenUrl != null ? null : webCredentialsUtils.getUserNameFromPreferences());
        password.setText(overriddenUrl != null ? null : webCredentialsUtils.getPasswordFromPreferences());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.server_requires_auth));
        builder.setMessage(context.getString(R.string.server_auth_credentials, overriddenUrl != null ? overriddenUrl : webCredentialsUtils.getServerFromPreferences()));
        builder.setView(dialogView);
        String finalOverriddenUrl = overriddenUrl;
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collect.getInstance().getActivityLogger().logAction(this, TAG, "OK");

                String userNameValue = username.getText().toString();
                String passwordValue = password.getText().toString();

                if (finalOverriddenUrl == null) {
                    webCredentialsUtils.saveCredentials(userNameValue, passwordValue);
                    webCredentialsUtils.setWebCredentialsFromPreferences();
                } else {
                    webCredentialsUtils.setWebCredentials(finalOverriddenUrl, username.getText().toString(), password.getText().toString());
                }

                resultListener.updatedCredentials();
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Collect.getInstance().getActivityLogger().logAction(this, TAG, "Cancel");

                        resultListener.cancelledUpdatingCredentials();
                    }
                });

        builder.setCancelable(false);

        return builder.create();
    }

    public interface AuthDialogUtilityResultListener {
        void updatedCredentials();

        void cancelledUpdatingCredentials();
    }
}
