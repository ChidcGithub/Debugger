mod logcat;
mod parser;
mod classifier;
mod exporter;
mod storage;

use std::sync::OnceLock;
use jni::JNIEnv;
use jni::objects::{GlobalRef, JClass, JObject, JString, JValue};
use jni::sys::{jint, jstring, jboolean, JNI_VERSION_1_6, JNI_TRUE, JNI_FALSE};

static VM: OnceLock<jni::JavaVM> = OnceLock::new();
static RUST_BRIDGE_CLASS: OnceLock<GlobalRef> = OnceLock::new();

fn get_vm() -> Option<&'static jni::JavaVM> {
    VM.get()
}

pub fn java_callback(method: &str, arg: &str) {
    let vm = match get_vm() {
        Some(v) => v,
        None => return,
    };
    let mut env = match vm.attach_current_thread() {
        Ok(e) => e,
        Err(_) => return,
    };
    let class_ref = match RUST_BRIDGE_CLASS.get() {
        Some(c) => c,
        None => return,
    };
    let raw = class_ref.as_raw();
    let obj = unsafe { JObject::from_raw(raw) };
    let class: JClass<'_> = obj.into();
    let j_arg = match env.new_string(arg) {
        Ok(s) => s,
        Err(_) => return,
    };
    let _ = env.call_static_method(
        &class,
        method,
        "(Ljava/lang/String;)V",
        &[JValue::Object(&j_arg)],
    );
}

#[no_mangle]
pub extern "system" fn JNI_OnLoad(
    vm: jni::JavaVM,
    _: *mut std::ffi::c_void,
) -> jint {
    if let Ok(mut env) = vm.attach_current_thread() {
        if let Ok(cls) = env.find_class("com/debugger/app/bridge/RustBridge") {
            if let Ok(global_ref) = env.new_global_ref(cls) {
                RUST_BRIDGE_CLASS.set(global_ref).ok();
            }
        }
    }
    VM.set(vm).ok();
    JNI_VERSION_1_6
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeInit<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    db_path: JString<'local>,
) {
    let path: String = match env.get_string(&db_path) {
        Ok(s) => s.into(),
        Err(_) => return,
    };
    storage::init(&path);
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeStartCapture(
    _env: JNIEnv<'_>,
    _class: JClass<'_>,
) {
    logcat::start_capture();
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeStopCapture(
    _env: JNIEnv<'_>,
    _class: JClass<'_>,
) {
    logcat::stop_capture();
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeGetLogs<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    filter_json: JString<'local>,
) -> jstring {
    let filter: String = env.get_string(&filter_json)
        .map(|s| s.into())
        .unwrap_or_default();
    let result = storage::get_logs(&filter);
    let output = match env.new_string(&result) {
        Ok(s) => s,
        Err(_) => return std::ptr::null_mut(),
    };
    output.into_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeExportLogs<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    path: JString<'local>,
    format: JString<'local>,
    filter_json: JString<'local>,
) -> jboolean {
    let path: String = env.get_string(&path)
        .map(|s| s.into())
        .unwrap_or_default();
    let format: String = env.get_string(&format)
        .map(|s| s.into())
        .unwrap_or_default();
    let filter: String = env.get_string(&filter_json)
        .map(|s| s.into())
        .unwrap_or_default();

    match exporter::export(&path, &format, &filter) {
        Ok(_) => JNI_TRUE,
        Err(_) => JNI_FALSE,
    }
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeClearLogs(
    _env: JNIEnv<'_>,
    _class: JClass<'_>,
) {
    storage::clear_logs();
}

#[no_mangle]
pub extern "system" fn Java_com_debugger_app_bridge_RustBridge_nativeGetStats<'local>(
    env: JNIEnv<'local>,
    _class: JClass<'local>,
) -> jstring {
    let result = storage::get_stats();
    let output = match env.new_string(&result) {
        Ok(s) => s,
        Err(_) => return std::ptr::null_mut(),
    };
    output.into_raw()
}
