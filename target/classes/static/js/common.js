function authFetch(url, options = {}) {
    const token = localStorage.getItem("token");
    if (!options.headers) options.headers = {};
    if (token) options.headers["Authorization"] = "Bearer " + token;
    if (options.body && typeof options.body === "object" && !(options.body instanceof FormData)) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(options.body);
    }
    return fetch(url, options).then(response => {
        if (response.status === 401) {
            localStorage.removeItem("token");
            window.location.href = "/login.html";
            return Promise.reject("未授权");
        }
        return response.json();
    });
}

function getVal(item, camel, underscore) {
    if (item == null) return "";
    if (item[camel] !== undefined && item[camel] !== null) return item[camel];
    if (item[underscore] !== undefined && item[underscore]) return item[underscore];
    return "";
}