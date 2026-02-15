package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.interfaces.FailurePresenter;
import com.github.kqfall1.java.interfaces.inputters.NumberInputter;
import com.github.kqfall1.java.javax.swing.AwtUtils;
import com.github.kqfall1.java.javax.swing.NumberJTextField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.concurrent.CompletionException;
import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required by a {@code MainMenuJFrame} to configure a new blackjack game.
 *
 * <p>Implements {@code FailurePresenter} to bolster application's user experience.</p>
 *
 * @author kqfall1
 * @since 14/02/2026
 */
public final class GameConfigJPanel extends JPanel implements FailurePresenter
{
    private final JCheckBox doublingDownOnSplitHandsAllowed;
    private final JLabel errorJLabel;
    private static final int JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE = 5;
    private static final Insets JTEXT_FIELD_PANEL_COMPONENT_INSETS = new Insets(JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE, JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE, JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE, JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE);
    private final NumberInputter minimumBetAmountInput;
    private final JLabel minimumBetAmountJLabel;
    private final JButton playButton;
    private final NumberInputter playerInitialChipsInput;
    private final JLabel playerInitialChipsJLabel;
    private final JSpinner shoeDeckCountInput;
    private final JLabel shoeDeckCountJLabel;
    private final NumberInputter shoePenetrationInput;
    private final JLabel shoePenetrationJLabel;
    private final JCheckBox shouldDealerHitOnSoft17;
    private final JCheckBox splittingAcesAllowed;
    private final JCheckBox surrenderingAllowed;
    private final JCheckBox surrenderingOnSplitHandsAllowed;

    public GameConfigJPanel()
    {
        final var MAIN_PANEL_DIMENSION = UiConstants.getGameConfigJDialogDimension();

        doublingDownOnSplitHandsAllowed = new JCheckBox(UiConstants.DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL);
        errorJLabel = new JLabel();
        errorJLabel.setMaximumSize(new Dimension(
            MAIN_PANEL_DIMENSION.width,
            errorJLabel.getHeight()
        ));
        minimumBetAmountInput = new NumberJTextField();
        minimumBetAmountJLabel = new JLabel(UiConstants.MINIMUM_BET_AMOUNT_LABEL);
        playButton = new JButton(UiConstants.PLAY_BUTTON_LABEL);
        playButton.addActionListener(this::readConfigValues);
        playerInitialChipsInput = new NumberJTextField();
        playerInitialChipsJLabel = new JLabel(UiConstants.PLAYER_INITIAL_CHIPS_LABEL);
        shoeDeckCountInput = new JSpinner(new SpinnerNumberModel(
            Shoe.MINIMUM_NUMBER_OF_DECKS,
            Shoe.MINIMUM_NUMBER_OF_DECKS,
            Shoe.MAXIMUM_NUMBER_OF_DECKS,
            1
        ));
        shoeDeckCountJLabel = new JLabel(UiConstants.SHOE_DECK_COUNT_LABEL);
        shoePenetrationInput = new NumberJTextField();
        shoePenetrationJLabel = new JLabel(UiConstants.SHOE_PENETRATION_LABEL);
        shouldDealerHitOnSoft17 = new JCheckBox(UiConstants.SHOULD_DEALER_HIT_ON_SOFT_17_LABEL);
        splittingAcesAllowed = new JCheckBox(UiConstants.SPLITTING_ACES_ALLOWED_LABEL);
        surrenderingAllowed = new JCheckBox(UiConstants.SURRENDERING_ALLOWED_LABEL);
        surrenderingOnSplitHandsAllowed = new JCheckBox(UiConstants.SURRENDERING_ON_SPLIT_HANDS_ALLOWED_LABEL);

        final var JCHECKBOX_PANEL_WRAPPER = new JPanel();
        JCHECKBOX_PANEL_WRAPPER.setLayout(new BoxLayout(JCHECKBOX_PANEL_WRAPPER, BoxLayout.Y_AXIS));
        JCHECKBOX_PANEL_WRAPPER.add(doublingDownOnSplitHandsAllowed);
        JCHECKBOX_PANEL_WRAPPER.add(shouldDealerHitOnSoft17);
        JCHECKBOX_PANEL_WRAPPER.add(splittingAcesAllowed);
        JCHECKBOX_PANEL_WRAPPER.add(surrenderingAllowed);
        JCHECKBOX_PANEL_WRAPPER.add(surrenderingOnSplitHandsAllowed);
        JCHECKBOX_PANEL_WRAPPER.setMaximumSize(JCHECKBOX_PANEL_WRAPPER.getPreferredSize());

        final var JTEXT_FIELD_PANEL_WRAPPER = new JPanel(new GridBagLayout());
        JTEXT_FIELD_PANEL_WRAPPER.add(minimumBetAmountJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add((NumberJTextField) minimumBetAmountInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(playerInitialChipsJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add((NumberJTextField) playerInitialChipsInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(shoeDeckCountJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(shoeDeckCountInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(shoePenetrationJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add((NumberJTextField) shoePenetrationInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            JTEXT_FIELD_PANEL_WRAPPER.getPreferredSize().height
        ));

        final var ERROR_LABEL_PANEL_WRAPPER = new JPanel(new GridBagLayout());
        ERROR_LABEL_PANEL_WRAPPER.add(errorJLabel);

        final var PLAY_BUTTON_PANEL_WRAPPER = new JPanel(new GridBagLayout());
        PLAY_BUTTON_PANEL_WRAPPER.add(playButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(JCHECKBOX_PANEL_WRAPPER);
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(JTEXT_FIELD_PANEL_WRAPPER);
        add(ERROR_LABEL_PANEL_WRAPPER);
        add(PLAY_BUTTON_PANEL_WRAPPER);
    }

    private Component[] getErrorRelatedComponents()
    {
        final var ALL_COMPONENTS = AwtUtils.getNestedComponents(this);
        final var ERROR_RELATED_COMPONENTS = new HashSet<Component>();

        for (Component component : ALL_COMPONENTS)
        {
            if (component instanceof NumberInputter)
            {
                ERROR_RELATED_COMPONENTS.add(component);
            }
        }

        ERROR_RELATED_COMPONENTS.add(playButton);
        return ERROR_RELATED_COMPONENTS.toArray(new Component[0]);
    }

    private GridBagConstraints getJLabelConstraints()
    {
        final var JLABEL_CONSTRAINTS = new GridBagConstraints();

        JLABEL_CONSTRAINTS.anchor = GridBagConstraints.WEST;
        JLABEL_CONSTRAINTS.fill = GridBagConstraints.NONE;
        JLABEL_CONSTRAINTS.gridx = 0;
        JLABEL_CONSTRAINTS.gridy = GridBagConstraints.RELATIVE;
        JLABEL_CONSTRAINTS.gridwidth = 1;
        JLABEL_CONSTRAINTS.gridheight = 1;
        JLABEL_CONSTRAINTS.insets = JTEXT_FIELD_PANEL_COMPONENT_INSETS;
        JLABEL_CONSTRAINTS.weightx = 0;
        JLABEL_CONSTRAINTS.weighty = 0;

        return JLABEL_CONSTRAINTS;
    }

    private GridBagConstraints getSecondaryJComponentConstraints()
    {
        final var SECONDARY_JCOMPONENT_CONSTRAINTS = new GridBagConstraints();

        SECONDARY_JCOMPONENT_CONSTRAINTS.fill = GridBagConstraints.HORIZONTAL;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridx = 1;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridy = GridBagConstraints.RELATIVE;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridwidth = 1;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridheight = 1;
        SECONDARY_JCOMPONENT_CONSTRAINTS.insets = JTEXT_FIELD_PANEL_COMPONENT_INSETS;
        SECONDARY_JCOMPONENT_CONSTRAINTS.weightx = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;
        SECONDARY_JCOMPONENT_CONSTRAINTS.weighty = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;

        return SECONDARY_JCOMPONENT_CONSTRAINTS;
    }

    private void readConfigValues(ActionEvent e)
    {
        try
        {
            final var MINIMUM_BET_AMOUNT = minimumBetAmountInput.getNumber(null, 1, Float.MAX_VALUE).join();
            final var PLAYER_INITIAL_CHIPS = playerInitialChipsInput.getNumber(null, 1, Float.MAX_VALUE).join();
            final var SHOE_DECK_COUNT = (int) shoeDeckCountInput.getValue();
            final var SHOE_PENETRATION = shoePenetrationInput.getNumber(null, Shoe.MINIMUM_PENETRATION, Shoe.MAXIMUM_PENETRATION).join();
        }
        catch (CompletionException ex)
        {
            presentMessage(ex.getMessage());
            updateGui(getErrorRelatedComponents());
            return;
        }

        //Construct config/engine/main game JFrame and start game
    }

    @Override
    public void presentMessage(String message)
    {
        errorJLabel.setText(message);
    }

    @Override
    public void updateGui(Component... components)
    {
        final var RED_BORDER = BorderFactory.createLineBorder(Color.RED);

        for (Component component : components)
        {
            final var JCOMPONENT = (JComponent) component;
            JCOMPONENT.setBorder(RED_BORDER);
        }
    }
}