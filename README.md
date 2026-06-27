# OneTap Full

Flutter 기반 초소형 MVP 웹 브라우저 앱입니다.

## 핵심 기능

- **WebView 기반 브라우저**: 실제 Android WebView 를 사용하여 웹페이지 표시
- **전체화면 모드**: 상태표시줄과 내비게이션 바를 숨기고 WebView 가 화면 전체를 차지
- **URL 입력 및 이동**: 상단에서 URL 입력 후 이동 가능 (scheme 없이 입력해도 자동 보정)
- **Material 3 디자인**: 깔끔하고 현대적인 UI

## 프로젝트 구조

```
/
├─ pubspec.yaml              # Flutter 의존성 설정
├─ lib/
│  └─ main.dart              # 메인 앱 코드
├─ android/                  # Android 네이티브 설정
│  ├─ app/
│  │  └─ src/main/
│  │     ├─ AndroidManifest.xml
│  │     ├─ kotlin/.../MainActivity.kt
│  │     └─ res/
│  ├─ build.gradle
│  └─ settings.gradle
├─ codemagic.yaml            # Codemagic CI/CD 설정
└─ README.md
```

## 기술 스택

- Flutter (Dart)
- webview_flutter ^4.8.0
- SystemChrome API (전체화면 제어)

## 로컬에서 빌드하는 방법

### 1. Flutter 환경 확인

```bash
flutter doctor
```

### 2. 의존성 설치

```bash
flutter pub get
```

### 3. APK 빌드

```bash
# 디버그 APK
flutter build apk --debug

# 릴리스 APK
flutter build apk --release
```

빌드된 APK 위치:
```
build/app/outputs/flutter-apk/release/app-release.apk
```

## Codemagic 에서 빌드하는 방법

1. GitHub 에 이 프로젝트를 업로드
2. Codemagic 에 로그인하고 프로젝트 연결
3. `codemagic.yaml` 이 자동으로 감지됨
4. 빌드 시작 → APK 생성

## 전체화면 기능 설명

- **전체화면 진입**: `SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky)`
- **전체화면 해제**: `SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge)`
- 전체화면 중에는 상단 URL 바와 하단 버튼바가 숨겨지고, 우측 상단의 플로팅 버튼으로 해제 가능

## Android 권한

- `INTERNET`: WebView 를 위한 인터넷 접근 권한
- `usesCleartextTraffic="true"`: HTTP 사이트 테스트 허용

## 라이선스

MIT
