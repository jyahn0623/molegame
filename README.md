# 두더지 잡기

## 프로젝트 개요
본 프로젝트는 Android OS 임베디드 기계에 설치되어 있는 안드로이드 어플리케이션입니다.

웹캠 카메라를 이용하여 수신된 프레임을 서버로 전달하여 서버에서 손바닥의 좌표를 반환하고,

해당 좌표를 이용하여 두더지를 잡았는지 안 잡았는지 판별하여 게임을 진행합니다.

프레임을 서버로 전달하기 위해 웹소켓 프로토콜을 사용하며, 서버에서 포즈 측정을 위해 Mediapipe의 Holistic을 사용합니다.

## 테스트 환경
- 임베디드 기계 : rockchip RT6X
- OS : Android 8.1
- 카메라 : ABKO WEB CAM
- 모니터 : 삼성 32인치 HDL-T320-OV-IR


## 파일 설명
- src/main/java/co/kr/molegame/GameMenuActivity : 초기 실행되는 액티비티 파일이며 훈련모드, 랜덤모드로 이동할 수 있습니다.
- src/main/java/co/kr/molegame/PracticeModeMenu : 랜덤모드를 클릭할 경우 실행되는 액티비티이며, Practice1 모드와 반랜덤모드로 이동할 수 있습니다.
- src/main/java/co/kr/molegame/MoleGame : 두더지 게임 관련 클래스 파일
- src/main/java/co/kr/molegame/MoleGameActivity : (랜덤모드) 두더지 게임 수행 액티비티
- src/main/java/co/kr/molegame/MoleGameSetting : (랜덤모드) 두더지 게임 수행을 위한 설정 액티비티
- src/main/java/co/kr/molegame/MoleGameResult : 두더지 게임이 종료된 후 보이는 액티비티
- src/main/java/co/kr/molegame/MoleSoundPool : 두더지 게임 시 사용될 효과음을 관리하는 클래스 파일
- src/main/java/co/kr/molegame/GameHalfRandomMode : (반랜덤모드) 두더지 게임 수행 액티비티
- src/main/java/co/kr/molegame/GameHalfRandomSetting : (반랜덤모드) 두더지 게임을 위한 설정 액티비티
- src/main/java/co/kr/molegame/GamePractice1Mode : (Practice1) 두더지 게임 수행 액티비티
- src/main/java/co/kr/molegame/GamePractice1ModeSetting : (Practice1) 두더지 게임을 위한 설정 액티비티
