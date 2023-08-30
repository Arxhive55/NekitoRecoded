package wtf.norma.nekito.ui.altmanager;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.norma.nekito.util.altmanager.GuiPasswordField;
import wtf.norma.nekito.util.shader.GLSL;

import java.io.IOException;

public final class GuiAltLogin extends GuiScreen {
//    private GuiTextField password;
    GuiPasswordField password;
    private final GuiScreen previousScreen;
    private GuiTextField username;
    private AltLoginThread thread;

    private String crackedStatus;
    public long init;


    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 1:
                    mc.displayGuiScreen(previousScreen);
                    break;

                case 0:
                    thread = new AltLoginThread(username.getText(), password.getText());
                    thread.start();
                    break;

                case 3:
                    thread = null;
                    thread = new AltLoginThread("nt_" + RandomStringUtils.random(8, true, true)              , "");
                    thread.start();
                    break;

                case 4:
                    break;

                case 5:
                    break;

                default:
                    break;
            }
        } catch (Throwable var11) {

        }
    }

    @Override
    public void drawScreen(int x, int y2, float z) {
        final FontRenderer font = mc.fontRendererObj;
        ScaledResolution scaledResolution = new ScaledResolution(mc);


        drawBackground();
        username.drawTextBox();
        password.drawTextBox();



        drawCenteredString(font, "Account Login", (int) (width / 2F), 20, -1);
        drawCenteredString(font, thread == null ? (crackedStatus == null ? EnumChatFormatting.GRAY + "Idle" : EnumChatFormatting.GREEN + crackedStatus) : thread.getStatus(), (int) (width / 2f), 29, -1);
        if (username.getText().isEmpty()) {
            font.drawStringWithShadow("Username", width / 2F - 96, 66, -7829368);
        }
        if (password.getText().isEmpty()) {
            font.drawStringWithShadow("Password", width / 2F - 96, 96, -7829368);
        }

        super.drawScreen(x, y2, z);
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;
        buttonList.add(new GuiButton(0, width / 2 - 100, var3 + 72 + 12, "Login"));
        buttonList.add(new GuiButton(1, width / 2 - 100, var3 + 72 + 12 + 24, I18n.format("gui.cancel")));
        buttonList.add(new GuiButton(3, width / 2 - 100, var3 + 72 + 12 + 48 + 24, "Generate Cracked Account"));
        username = new GuiTextField(var3, mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        username.setFocused(true);
        password = new GuiPasswordField(mc.fontRendererObj, width / 2 - 100, 90, 200, 20);
        password.setFocused(false);
        init = System.currentTimeMillis();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!username.isFocused() && !password.isFocused()) {
                username.setFocused(true);
            } else {
                username.setFocused(password.isFocused());
                password.setFocused(!username.isFocused());
            }
        }
        if (character == '\r') {
            actionPerformed(buttonList.get(0));
        }
        username.textboxKeyTyped(character, key);
        password.textboxKeyTyped(character, key);
    }



    @Override
    protected void mouseClicked(int x, int y2, int button) {
        try {
            super.mouseClicked(x, y2, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        username.mouseClicked(x, y2, button);
        password.mouseClicked(x, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();

    }

    public void drawBackground() {
        GL11.glPushMatrix();
        {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GLSL.MAINMENU.useShader(width, height, 0, 0, (System.currentTimeMillis() - init) / 1000f);


            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2f(0, 0);
                GL11.glVertex2f(0, height);
                GL11.glVertex2f(width, height);
                GL11.glVertex2f(width, 0);
                GL11.glEnd();
            }
            GL20.glUseProgram(0);
        }
        GL11.glPopMatrix();
    }
}
