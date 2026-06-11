const designerTokenKey = 'designer-unknown-dev-access-token';

function usePreferencesStore() {
    return {
        enableFooter: false,
        fixedFooter: false,
    };
}
usePreferencesStore.getState = () => {
    return {
        language: "zh_CN",
    };
}

function useAuthStore() {
    return {

    };
}
useAuthStore.getState = () => {
    const accessTokenJSON = window.localStorage.getItem(designerTokenKey);
    return {
        ...JSON.parse(accessTokenJSON ?? '{"state": {"token": "", "refreshToken": ""} }').state,

        reset() { window.localStorage.removeItem(designerTokenKey); },
    };
};

function useGlobalStore() {
    return {

    };
}
useGlobalStore.getState = () => {
    return {
        openGlobalSpin() {},
        closeGlobalSpin() {},
    };
}

export {
    useAuthStore,
    useGlobalStore,
    usePreferencesStore,
};
