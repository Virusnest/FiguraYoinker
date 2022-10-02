package dev.virusnest.yoinker.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.avatars.providers.LocalAvatarLoader;
import org.moon.figura.gui.FiguraToast;
import org.moon.figura.gui.widgets.TextField;
import org.moon.figura.gui.widgets.TexturedButton;
import org.moon.figura.gui.widgets.lists.PlayerList;
import org.moon.figura.gui.widgets.trust.PlayerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.moon.figura.gui.screens.TrustScreen;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(TrustScreen.class)
public class YoinkerMixin extends Screen{

	@Shadow private TextField uuid;
	@Shadow private TexturedButton back;
	@Shadow private PlayerList playerList;
	int middle = this.width / 2;
	int listWidth = Math.min(middle - 6, 208);
	@Shadow private TexturedButton yoink = new TexturedButton(middle + listWidth - 18, back.y - 24, 20, 20, Text.literal("yoink"), Text.literal("Set the selected player's avatar"), button -> {
		String text = uuid.getField().getText();
		UUID id;

		try {
			id = UUID.fromString(text);
		} catch (Exception ignored) {
			id = FiguraMod.playerNameToUUID(text);
		}

		if (id == null) {
			FiguraToast.sendToast("oopsie", FiguraToast.ToastType.ERROR);
			return;
		}

		Avatar avatar = AvatarManager.getAvatarForPlayer(id);
		if (avatar == null || avatar.nbt == null)
			return;

		if (playerList.selectedEntry instanceof PlayerElement player) {
			UUID target = player.getOwner();
			if (FiguraMod.isLocal(target))
				AvatarManager.localUploaded = false;

			AvatarManager.setAvatar(target, avatar.nbt);
			LocalAvatarLoader.saveNbt(avatar.nbt);
			FiguraToast.sendToast("yoinked");
		}
	});

	protected YoinkerMixin(Text title) {
		super(title);
	}

	@Inject(method = "init()V", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		addDrawableChild(uuid);
		addDrawableChild(yoink);

	}
}

