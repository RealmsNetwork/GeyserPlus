package dev.letsgoaway.geyserextras.core.form;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import lombok.Setter;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BedrockMenu {
    private SimpleForm.Builder simpleForm;
    private final List<Button> buttons;


    @Setter
    private String title = "";

    @Setter
    private String header = "";

    public BedrockMenu() {
        simpleForm = SimpleForm.builder();
        buttons = new ArrayList<>();
    }

    public void onClose(ExtrasPlayer player) {
    }

    public void onButtonClick(ExtrasPlayer player) {
    }

    public BedrockMenu add(Button button) {
        if (button != null) {
            if (button.getSortOrder() == Integer.MIN_VALUE) {
                button.setSortOrder(buttons.size() * 10);
            }
            buttons.add(button);
        }
        return this;
    }

    public SimpleForm create(ExtrasPlayer player) {
        simpleForm = SimpleForm.builder();
        simpleForm.title(this.title)
                .content(this.header);
        List<Button> orderedButtons = new ArrayList<>(buttons);
        orderedButtons.sort(Comparator.comparingInt(Button::getSortOrder));
        for (Button button : orderedButtons) {
            simpleForm.button(button.get());
        }
        simpleForm.validResultHandler(response -> {
            int clicked = response.clickedButtonId();
            if (clicked < 0 || clicked >= orderedButtons.size()) {
                // Ignore malformed button IDs without invoking close handlers or menu actions.
                return;
            }
            Button button = orderedButtons.get(clicked);
            if (button.onSelect != null) {
                button.onSelect.run();
                // A visually disabled button must not trigger menu-specific post-click actions.
                if (!button.isDisabled()) {
                    onButtonClick(player);
                }
            }
        });

        simpleForm.closedOrInvalidResultHandler(() -> {
            onClose(player);
        });

        return simpleForm.build();
    }
}
