import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const OneTapFullApp());
}

class OneTapFullApp extends StatelessWidget {
  const OneTapFullApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'OneTap Full',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      home: const MainPage(),
    );
  }
}

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  late final WebViewController _webViewController;
  final TextEditingController _urlController = TextEditingController(text: 'https://google.com');
  bool _isFullScreen = false;

  @override
  void initState() {
    super.initState();
    
    // WebView 초기화
    _webViewController = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(const Color(0x00000000))
      ..setNavigationDelegate(
        NavigationDelegate(
          onPageStarted: (String url) {
            setState(() {
              _urlController.text = url;
            });
          },
          onPageFinished: (String url) {
            setState(() {
              _urlController.text = url;
            });
          },
        ),
      )
      ..loadRequest(Uri.parse('https://google.com'));
  }

  @override
  void dispose() {
    _urlController.dispose();
    super.dispose();
  }

  /// 전체화면 모드 진입/해제
  Future<void> _toggleFullScreen() async {
    setState(() {
      _isFullScreen = !_isFullScreen;
    });

    if (_isFullScreen) {
      // 전체화면 진입: 상태표시줄과 내비게이션 바 숨김
      await SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    } else {
      // 전체화면 해제: 원래 UI 복귀
      await SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
    }
  }

  /// URL 이동 처리
  void _navigateToUrl() {
    String url = _urlController.text.trim();
    
    // scheme 이 없으면 https:// 추가
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      url = 'https://$url';
    }
    
    try {
      _webViewController.loadRequest(Uri.parse(url));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('잘못된 URL 형식입니다: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
        // 전체화면 중이면 먼저 해제
        if (_isFullScreen) {
          await _toggleFullScreen();
          return false;
        }
        // WebView 뒤로가기 가능하면 뒤로가기
        if (await _webViewController.canGoBack()) {
          _webViewController.goBack();
          return false;
        }
        return true;
      },
      child: Scaffold(
        body: Stack(
          children: [
            // WebView (화면 대부분 차지)
            Column(
              children: [
                // 상단 URL 입력 영역 (전체화면일 때 숨김)
                if (!_isFullScreen) _buildUrlBar(),
                // WebView
                Expanded(
                  child: WebViewWidget(controller: _webViewController),
                ),
                // 하단 버튼바 (전체화면일 때 숨김)
                if (!_isFullScreen) _buildButtonBar(),
              ],
            ),
            // 전체화면 시 표시되는 플로팅 해제 버튼
            if (_isFullScreen) _buildFloatingExitButton(),
          ],
        ),
      ),
    );
  }

  /// 상단 URL 입력바 위젯
  Widget _buildUrlBar() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerHighest,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _urlController,
              decoration: InputDecoration(
                hintText: 'URL 입력 (예: google.com)',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(25),
                ),
                contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                prefixIcon: const Icon(Icons.public),
              ),
              onSubmitted: (_) => _navigateToUrl(),
              textInputAction: TextInputAction.go,
            ),
          ),
          const SizedBox(width: 8),
          ElevatedButton.icon(
            onPressed: _navigateToUrl,
            icon: const Icon(Icons.arrow_forward),
            label: const Text('이동'),
            style: ElevatedButton.styleFrom(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(25),
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// 하단 버튼바 위젯
  Widget _buildButtonBar() {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerHighest,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 4,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          // 이동 버튼
          Expanded(
            child: ElevatedButton.icon(
              onPressed: _navigateToUrl,
              icon: const Icon(Icons.arrow_forward),
              label: const Text('이동'),
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
            ),
          ),
          const SizedBox(width: 12),
          // 전체화면 버튼 (강조)
          Expanded(
            flex: 2,
            child: ElevatedButton.icon(
              onPressed: _toggleFullScreen,
              icon: Icon(_isFullScreen ? Icons.fullscreen_exit : Icons.fullscreen),
              label: Text(_isFullScreen ? '전체화면 해제' : '전체화면'),
              style: ElevatedButton.styleFrom(
                backgroundColor: Theme.of(context).colorScheme.primary,
                foregroundColor: Theme.of(context).colorScheme.onPrimary,
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                elevation: 4,
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// 전체화면 시 표시되는 플로팅 해제 버튼
  Widget _buildFloatingExitButton() {
    return Positioned(
      top: 16,
      right: 16,
      child: GestureDetector(
        onTap: _toggleFullScreen,
        child: Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: Colors.black.withOpacity(0.6),
            shape: BoxShape.circle,
          ),
          child: const Icon(
            Icons.fullscreen_exit,
            color: Colors.white,
            size: 28,
          ),
        ),
      ),
    );
  }
}
