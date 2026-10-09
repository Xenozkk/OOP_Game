package game;

/**
 * เก็บสถานะต่างๆ ของเกม (State Machine)
 */
public enum GameState {
    TITLE,          // หน้าเมนูหลัก
    CHAR_SELECT,    // หน้าเลือกตัวละคร
    WEAPON_SELECT,  // หน้าเลือกอาวุธ
    PLAYING,        // กำลังเล่นเกม
    PAUSED,         // หยุดเกมชั่วคราว
    SHOP,           // หน้าจอร้านค้าหลังจบเวฟ
    GAME_OVER       // หน้าจอตอนตาย
}
