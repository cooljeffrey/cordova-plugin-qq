var exec = require('cordova/exec');

module.exports = {
    TITLE_MAX_LENGTH: 45,
    SUMMARY_MAX_LENGTH: 60,
    KEY_IMAGE_URL: "imageUrl",
    KEY_IMAGE_LOCAL_URL: "imageLocalUrl",
    KEY_TITLE: "title",
    KEY_SUMMARY: "summary",
    KEY_SITE: "site",
    KEY_TARGET_URL: "targetUrl",
    KEY_APP_NAME: "appName",
    KEY_AUDIO_URL: "audio_url",
    KEY_TYPE: "req_type",
    KEY_EXT_STR: "share_qq_ext_str",
    KEY_EXT_INT: "cflag",
    KEY_FLAG_QZONE_AUTO_OPEN: 1,
    KEY_FLAG_QZONE_ITEM_HIDE: 2,
    KEY_TYPE_DEFAULT: 1,
    KEY_TYPE_AUDIO: 2,
    KEY_TYPE_IMAGE: 5,
    KEY_TYPE_APP: 6,
    share: function (message, onSuccess, onError) {
        exec(onSuccess, onError, "qq", "share", [message]);
    }
};