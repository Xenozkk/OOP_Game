package game;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // สร้างหน้าต่างเกม
        JFrame window = new JFrame("Brotato Clone - Java OOP Project");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        window.pack();
        
        // จัดให้อยู่กึ่งกลางหน้าจอ
        window.setLocationRelativeTo(null); 
        
        // แสดงหน้าต่าง
        window.setVisible(true);
        
        gamePanel.startGameThread();
        
        System.out.println("✅ Game Window Started Successfully in new path!");
    }
}
