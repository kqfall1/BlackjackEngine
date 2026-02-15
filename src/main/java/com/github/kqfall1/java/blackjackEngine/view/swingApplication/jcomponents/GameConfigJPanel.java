package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.interfaces.ErrorPresenter;
import com.github.kqfall1.java.javax.swing.NumberJTextField;
import com.github.kqfall1.java.managers.InputManager;
import java.awt.*;
import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required by a {@code MainMenuJFrame} to configure a new blackjack game.
 *
 * <p>Encapsulates an {@code InputManager} that accepts input from {@code NumberJTextField} and
 * {@code StringJTextField} objects. Implements {@code ErrorPresenter} to improve the application's user experience.</p>
 *
 * @author kqfall1
 * @since 14/02/2026
 */
public final class GameConfigJPanel extends JPanel implements ErrorPresenter
{
    private JCheckBox doublingDownOnSplitHandsAllowed;
    private static final int JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE = 5;
    private InputManager inputManager;
    private static final Insets JTEXT_FIELD_PANEL_COMPONENT_INSETS = new Insets(JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE, JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE, JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE, JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE);
    private JCheckBox shouldDealerHitOnSoft17;
    private JCheckBox splittingAcesAllowed;
    private JCheckBox surrenderingAllowed;
    private JCheckBox surrenderingOnSplitHandsAllowed;

    public GameConfigJPanel()
    {
        doublingDownOnSplitHandsAllowed = new JCheckBox(UiConstants.DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL);
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
        JTEXT_FIELD_PANEL_WRAPPER.add(new JLabel(UiConstants.MINIMUM_BET_AMOUNT_LABEL), getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(new NumberJTextField(), getJTextFieldConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(new JLabel(UiConstants.PLAYER_INITIAL_CHIPS_LABEL), getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(new NumberJTextField(), getJTextFieldConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            JTEXT_FIELD_PANEL_WRAPPER.getPreferredSize().height
        ));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(JCHECKBOX_PANEL_WRAPPER);
        add(Box.createVerticalStrut(UiConstants.DEFAULT_MARGIN_VALUE));
        add(JTEXT_FIELD_PANEL_WRAPPER);
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

    private GridBagConstraints getJTextFieldConstraints()
    {
        final var JTEXT_FIELD_CONSTRAINTS = new GridBagConstraints();

        JTEXT_FIELD_CONSTRAINTS.fill = GridBagConstraints.HORIZONTAL;
        JTEXT_FIELD_CONSTRAINTS.gridx = 1;
        JTEXT_FIELD_CONSTRAINTS.gridy = GridBagConstraints.RELATIVE;
        JTEXT_FIELD_CONSTRAINTS.gridwidth = 1;
        JTEXT_FIELD_CONSTRAINTS.gridheight = 1;
        JTEXT_FIELD_CONSTRAINTS.insets = JTEXT_FIELD_PANEL_COMPONENT_INSETS;
        JTEXT_FIELD_CONSTRAINTS.weightx = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;
        JTEXT_FIELD_CONSTRAINTS.weighty = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;

        return JTEXT_FIELD_CONSTRAINTS;
    }

    @Override
    public void showError(String message)
    {

    }

    @Override
    public void showException(Exception e)
    {

    }
}