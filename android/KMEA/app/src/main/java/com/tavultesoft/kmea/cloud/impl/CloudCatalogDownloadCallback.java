package com.tavultesoft.kmea.cloud.impl;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tavultesoft.kmea.KeyboardPickerActivity;
import com.tavultesoft.kmea.R;
import com.tavultesoft.kmea.cloud.CloudApiTypes;
import com.tavultesoft.kmea.cloud.CloudDataJsonUtil;
import com.tavultesoft.kmea.cloud.ICloudDownloadCallback;
import com.tavultesoft.kmea.data.CloudRepository;
import com.tavultesoft.kmea.data.Dataset;
import com.tavultesoft.kmea.data.Keyboard;
import com.tavultesoft.kmea.data.LanguageResource;
import com.tavultesoft.kmea.data.LexicalModel;
import com.tavultesoft.kmea.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for cloud catalogue download.
 * Is used for download with progress and download with Clientdownloadmanager.
 */
public class CloudCatalogDownloadCallback implements ICloudDownloadCallback<Dataset, CloudCatalogDownloadReturns> {

  private static final String TAG = "CloudCatalogDownloadCb";

  /**
   * Forces update process for all installed keyboard bundles.
   * Only for testing, should be false for merging
   */
  private static final boolean DEBUG_SIMULATE_UPDATES = false;

  private final Context context;

  // These keyboard query callback parameters actually aren't used at present.
  private final Runnable querySuccess;
  private final Runnable failure;
  private final CloudRepository.UpdateHandler updateHandler;


  public CloudCatalogDownloadCallback(Context context, CloudRepository.UpdateHandler updateHandler, Runnable success, Runnable failure)
  {
    this.context = context;


    Runnable dummy = new Runnable() {
      public void run() {
        // Do nothing.
      }
    };
    this.querySuccess = success != null ? success : dummy;
    this.failure = failure != null ? failure : dummy;

    this.updateHandler = updateHandler != null ? updateHandler : new CloudRepository.UpdateHandler() {
      @Override
      public void onUpdateDetection(List<Bundle> updateBundles) {
        // Do nothing.
        return;
      }
    };
  }
  private Bundle updateCheck(LanguageResource cloudResource, LanguageResource existingMatch) {
    if (DEBUG_SIMULATE_UPDATES) {
      return cloudResource.buildDownloadBundle();
    }

    if (compareVersions(cloudResource, existingMatch) == FileUtils.VERSION_GREATER) {
      return cloudResource.buildDownloadBundle();
    } else {
      return null;
    }
  }

  private int compareVersions(LanguageResource addition, LanguageResource original) {
    // Get version from newly-downloaded keyboard.
    String addVersion = addition.getVersion();
    String origVersion = original.getVersion();

    int result = FileUtils.compareVersions(addVersion, origVersion);

    if (DEBUG_SIMULATE_UPDATES && result == FileUtils.VERSION_EQUAL) {
      // Ensures that we preserve the cloud-based version that provides download URLs.
      return FileUtils.VERSION_GREATER;
    } else {
      return result;
    }
  }

  public void handleDownloadError()
  {
    failure.run();
  }

  void saveDataToCache(CloudCatalogDownloadReturns jsonTuple)
  {
    // First things first - we've successfully downloaded from the Cloud.  Cache that stuff!
    if (jsonTuple.keyboardJSON != null) {
      CloudDataJsonUtil.saveJSONObjectToCache(CloudDataJsonUtil.getKeyboardCacheFile(context), jsonTuple.keyboardJSON);
    }
    if (jsonTuple.lexicalModelJSON != null) {
      CloudDataJsonUtil.saveJSONArrayToCache(CloudDataJsonUtil.getLexicalModelCacheFile(context), jsonTuple.lexicalModelJSON);
    }
  }


    private JSONArray ensureInit(Context aContext,Dataset aDataSet, JSONArray json) {
    if (json == null && aDataSet.isEmpty()) {
      Toast.makeText(context, "Failed to access Keyman server!", Toast.LENGTH_SHORT).show();
      handleDownloadError();
      return null;
    }

    return (json != null) ? json : new JSONArray();
  }

   private JSONObject ensureInit(Context aContext,Dataset aDataSet, JSONObject json) {
    if (json == null && aDataSet.isEmpty()) {
      Toast.makeText(context, "Failed to access Keyman server!", Toast.LENGTH_SHORT).show();
      handleDownloadError();
      return null;
    }

    return (json != null) ? json : new JSONObject();
  }

  protected void ensureInitCloudReturn(Context aContext, Dataset aDataSet, CloudCatalogDownloadReturns jsonTuple)
  {
    jsonTuple.keyboardJSON = ensureInit(aContext,aDataSet,jsonTuple.keyboardJSON);
    jsonTuple.lexicalModelJSON = ensureInit(aContext,aDataSet, jsonTuple.lexicalModelJSON);
  }

  public void processCloudReturns(Dataset aDataSet, CloudCatalogDownloadReturns jsonTuple, boolean executeCallbacks) {
    // Only empty if no queries returned data - we're offline.
    if (jsonTuple.isEmpty()) {
      this.failure.run(); // Signal failure to download to our failure callback.
      CloudRepository.shared.updateFinished();
      return;
    }

    List<Keyboard> keyboardsArrayList = CloudDataJsonUtil.processKeyboardJSON(jsonTuple.keyboardJSON, false);
    List<LexicalModel> lexicalModelsArrayList = CloudDataJsonUtil.processLexicalModelJSON(jsonTuple.lexicalModelJSON);

    Dataset installedData = KeyboardPickerActivity.getInstalledDataset(context);
    final List<Bundle> updateBundles = new ArrayList<>();

    // We're about to do a big batch of edits.
    aDataSet.setNotifyOnChange(false);

    // Filter out any duplicates from KMP keyboards, properly merging the lists.
    for (int i = 0; i < keyboardsArrayList.size(); i++) {
      Keyboard keyboard = keyboardsArrayList.get(i);

      // Check for duplicates / possible updates.
      Keyboard match = aDataSet.keyboards.findMatch(keyboard);

      if (match != null) {
        if (compareVersions(keyboard, match) == FileUtils.VERSION_GREATER) {
          aDataSet.keyboards.remove(match);
        } else {
          keyboardsArrayList.remove(keyboard);
          i--; // Decrement our index to reflect the removal.
        }
      } // else no match == no special handling.
    }

    // Add cloud-returned keyboard info to the CloudRepository's KeyboardsAdapter.
    aDataSet.keyboards.addAll(keyboardsArrayList);

    // The actual update check.
    for (int i = 0; i < installedData.keyboards.getCount(); i++) {
      Keyboard keyboard = installedData.keyboards.getItem(i);

      // Check for duplicates / possible updates.
      Keyboard match = aDataSet.keyboards.findMatch(keyboard);

      if (match != null) {
        Bundle bundle = updateCheck(match, keyboard);
        if (bundle != null) {
          updateBundles.add(bundle);
        }
      } // else no match == no special handling.
    }

    // Filter out any duplicates from already-installed models, properly merging the lists.
    for (int i = 0; i < lexicalModelsArrayList.size(); i++) {
      LexicalModel model = lexicalModelsArrayList.get(i);

      // Check for duplicates / possible updates.
      LexicalModel match = aDataSet.lexicalModels.findMatch(model);

      if (match != null) {
        if (compareVersions(model, match) == FileUtils.VERSION_GREATER) {
          aDataSet.lexicalModels.remove(match);
        } else {
          lexicalModelsArrayList.remove(model);
          i--; // Decrement our index to reflect the removal.
        }
      } // else no match == no special handling.
    }

    // Add the cloud-returned lexical model info to the CloudRepository's lexical models adapter.
    aDataSet.lexicalModels.addAll(lexicalModelsArrayList);

    // Do the actual update checks.
    for (int i = 0; i < installedData.lexicalModels.getCount(); i++) {
      LexicalModel model = installedData.lexicalModels.getItem(i);

      // Check for duplicates / possible updates.
      LexicalModel match = aDataSet.lexicalModels.findMatch(model);

      if (match != null) {
        Bundle bundle = updateCheck(match, model);
        if (bundle != null) {
          updateBundles.add(bundle);
        }
      } // else no match == no special handling.
    }

    if (updateBundles.size() > 0 && !(DEBUG_SIMULATE_UPDATES && !executeCallbacks)) {
      // Time for updates!
      Log.v(TAG, "Performing keyboard and model updates for " + updateBundles.size() + " resources.");

      updateHandler.onUpdateDetection(updateBundles);
    }

    // And finish.
    aDataSet.notifyDataSetChanged(); // Edits are done - signal that.

    if (executeCallbacks) {
      querySuccess.run();
    }
  }

  @Override
  public void initializeContext(Context context) {

  }

  @Override
  public void applyCloudDownloadToModel(Context aContext, Dataset aDataSet, CloudCatalogDownloadReturns aCloudResult)
  {
    saveDataToCache(aCloudResult);

    ensureInitCloudReturn(aContext,aDataSet,aCloudResult);

    processCloudReturns(aDataSet, aCloudResult,true);

    CloudRepository.shared.updateFinished();
    aDataSet.notifyDataSetChanged();
  }

  @Override
  public CloudCatalogDownloadReturns extractCloudResultFromDownloadSet(
    CloudApiTypes.CloudDownloadSet<Dataset, CloudCatalogDownloadReturns> aDownload)
  {
    List<CloudApiTypes.CloudApiReturns> retrievedJSON = new ArrayList<>(aDownload.getSingleDownloads().size());

    for (CloudApiTypes.SingleCloudDownload _d : aDownload.getSingleDownloads()) {

      CloudApiTypes.CloudApiReturns _json_result = CloudDataJsonUtil.retrieveJsonFromDownload(_d);

      if (_json_result!=null)
        retrievedJSON.add(_json_result);  // Null if offline.
    }

    return new CloudCatalogDownloadReturns(retrievedJSON);
  }
}
