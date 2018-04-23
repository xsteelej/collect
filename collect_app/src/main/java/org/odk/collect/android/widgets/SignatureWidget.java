/*
 * Copyright (C) 2012 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.DrawActivity;
import org.odk.collect.android.application.Collect;

import java.io.File;

import static org.odk.collect.android.utilities.ApplicationConstants.RequestCodes;

/**
 * Signature widget.
 *
 * @author BehrAtherton@gmail.com
 */
public class SignatureWidget extends BaseImageWidget {

    private Button signButton;

    public SignatureWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);
        super.setup();
    }

    @Override
    protected void setupButtons() {
        signButton = super.setupSingleButton(getContext().getString(R.string.sign_button),R.id.simple_button);
    }

    @Override
    public void setButtonText() {
        signButton.setText(getContext().getString(R.string.sign_button));
    }

    @Override
    protected void setOnLongClickListenerForButtons(OnLongClickListener l) {
        signButton.setOnLongClickListener(l);
    }

    @Override
    protected void cancelLongPressForButtons() {
        signButton.cancelLongPress();
    }

    @Override
    protected String loggerContextString() {
        return "signButton";
    }

    @Override
    protected void launchActivity() {
        super.launchDrawActivity(
                DrawActivity.OPTION_SIGNATURE,
                RequestCodes.SIGNATURE_CAPTURE,
                getContext().getString(R.string.signature_capture));
    }
}
