// jsr-310.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"


// #include "winternl.h"

// static NTSTATUS (WINAPI *pfQuerySystemTime)(LARGE_INTEGER*);

extern "C" jint JNIEXPORT JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
	//HMODULE lib = LoadLibraryA("Ntdll");
	//pfQuerySystemTime = (NTSTATUS (WINAPI*)(LARGE_INTEGER*))GetProcAddress(lib, "NtQuerySystemTime");
	return JNI_VERSION_1_2;
}

extern "C" jlong JNIEXPORT JNICALL Java_javax_time_impl_WindowsSystemTime_get(JNIEnv *env, jclass cls) {
	union {
		FILETIME fileTime;
		LARGE_INTEGER time;
		jlong time64;
	} t;
	GetSystemTimeAsFileTime(&t.fileTime);
	// (*pfQuerySystemTime)(&t.time);
	return t.time64;
}

extern "C" jlong JNIEXPORT JNICALL Java_javax_time_impl_WindowsSystemTime_getAdjustment(JNIEnv *env, jclass cls) {
	DWORD timeAdjustment;
	DWORD timeIncrement;
	BOOL disabled;
	BOOL result = GetSystemTimeAdjustment(&timeAdjustment, &timeIncrement, &disabled);
	if (!result)
		return -1;
	jlong t = (((jlong)timeAdjustment)<< 32) | ((jlong)timeIncrement);
	if (disabled)
		t |= (((jlong)1) << 63);
	return t;
}