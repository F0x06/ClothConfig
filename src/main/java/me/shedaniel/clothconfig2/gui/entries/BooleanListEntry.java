package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class BooleanListEntry extends TooltipListEntry<Boolean> {
    
    private final AtomicBoolean bool;
    private final ButtonWidget buttonWidget;
    private final ButtonWidget resetButton;
    private final Consumer<Boolean> saveConsumer;
    private final Supplier<Boolean> defaultValue;
    private final List<Element> widgets;
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(String fieldName, boolean bool, Consumer<Boolean> saveConsumer) {
        this(fieldName, bool, "text.cloth-config.reset_value", null, saveConsumer);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(String fieldName, boolean bool, String resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(String fieldName, boolean bool, String resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Supplier<Optional<String[]>> tooltipSupplier) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(String fieldName, boolean bool, String resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.bool = new AtomicBoolean(bool);
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, "", widget -> {
            BooleanListEntry.this.bool.set(!BooleanListEntry.this.bool.get());
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(resetButtonKey)) + 6, 20, I18n.translate(resetButtonKey), widget -> {
            BooleanListEntry.this.bool.set(defaultValue.get());
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public Boolean getValue() {
        return bool.get();
    }
    
    @Override
    public Optional<Boolean> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != bool.get();
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(getYesNoText(bool.get()));
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(getFieldName())), y + 5, 16777215);
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), x, y + 5, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
        }
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(mouseX, mouseY, delta);
        buttonWidget.render(mouseX, mouseY, delta);
    }
    
    public String getYesNoText(boolean bool) {
        return I18n.translate("text.cloth-config.boolean.value." + bool);
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
}
