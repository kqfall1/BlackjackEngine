//Quinn Keenan, 301504914, 02/09/2025

using BlackjackLibrary;
using System.Diagnostics;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace WindowsFormsBlackjackApplication
{
    public partial class BlackjackAppForm : Form
    {
        private const string CARD_IMAGES_PATH = @"Images\Cards";

        private PlayerHand currentPlayerHand; 
        public PlayerHand CurrentPlayerHand
        {
            get
            {
                return currentPlayerHand; 
            }
            set
            {
                currentPlayerHand = value;

                if (currentPlayerHand.HandType is HandType.Main)
                {
                    CurrentPlayerPanel = GameplayPlayerMainHandPanel;
                    CurrentPlayerScoreLbl = GameplayPlayerMainHandScoreLbl;
                }
                else
                {
                    CurrentPlayerPanel = GameplayPlayerSplitHandPanel;
                    CurrentPlayerScoreLbl = GameplayPlayerSplitHandScoreLbl; 
                }
            }
        }

        private Label currentPlayerHandScoreLbl; 
        public Label CurrentPlayerScoreLbl
        {
            get
            {
                return currentPlayerHandScoreLbl;
            }
            set
            {
                currentPlayerHandScoreLbl = value;
            }
        }

        private FlowLayoutPanel currentPlayerPanel; 
        public FlowLayoutPanel CurrentPlayerPanel
        {
            get
            {
                return currentPlayerPanel;
            }
            set
            {
                if (currentPlayerPanel is not null)
                {
                    currentPlayerPanel.BackColor = Color.Transparent;
                }
                 
                currentPlayerPanel = value;
                currentPlayerPanel.BackColor = Color.Cornsilk; 
            }
        }

        public BlackjackAppForm()
        {
            InitializeComponent();
            IntroQuitBtn.Click += (object sender, EventArgs e) => Application.Exit();
            GameplayDoubleDownBtn.Click += (object sender, EventArgs e) => GameController.PlayerAction(PlayerInputAbbreviation.D, CurrentPlayerHand);
            GameplayHitBtn.Click += (object sender, EventArgs e) => GameController.PlayerAction(PlayerInputAbbreviation.H, CurrentPlayerHand);
            GameplayBetAndYesOrNoBtn.Click += (object sender, EventArgs e) => GameplayInsuranceBetAndYesOrNoBtn_Click(sender, e);
            GameplayNextBetOrHandBtn.Click += (object sender, EventArgs e) => ResetFrontendAfterShowdown(sender, e);
            GameplaySplitBtn.Click += (object sender, EventArgs e) => GameController.PlayerAction(PlayerInputAbbreviation.SP, CurrentPlayerHand);
            GameplayStandBtn.Click += (object sender, EventArgs e) => GameController.PlayerAction(PlayerInputAbbreviation.ST, CurrentPlayerHand);
            GameplayerSurrenderBtn.Click += (object sender, EventArgs e) => GameController.PlayerAction(PlayerInputAbbreviation.SU, CurrentPlayerHand);
            MessageManager.LatestMessageChanged += (object sender, GameNotificationEventArgs e) => GameplayMessageAndPromptLbl.Text = e.Message;
            SwitchPanels(IntroPanel);
        }

        private void AlterGuiAfterEntityBankrupts(object sender, BlackjackEntityBankruptsEventArgs e)
        {
            GameplayDealerHandPanel.Controls.Clear();
            GameplayDealerChipsLbl.Text = MessageManager.DEFAULT_DEALER_CHIPS_LABEL_TEXT; 
            GameplayDealerScoreLbl.Text = MessageManager.DEFAULT_DEALER_SCORE_LABEL_TEXT; 
            GameplayPlayerMainHandPanel.Controls.Clear();
            GameplayPlayerSplitHandPanel.Controls.Clear();
            GameplayPlayerChipsLbl.Text = MessageManager.DEFAULT_PLAYER_CHIPS_LABEL_TEXT; 
            GameplayPlayerMainHandScoreLbl.Text = MessageManager.DEFAULT_PLAYER_SCORE_LABEL_TEXT;
            GameplayPlayerSplitHandScoreLbl.Text = MessageManager.DEFAULT_PLAYER_SCORE_LABEL_TEXT;
            SwitchPanels(IntroPanel); 
        }

        private void AlterCurrentPlayerHandAfterPlayerHandIsSet(object sender, PlayerDrawingHandSetEventArgs e)
        {
            CurrentPlayerHand = e.DrawingHand;

            //Clears the second card of the main hand panel if the player has just split.
            if (e.DrawingHand is PlayerHand playerHand && playerHand.HandType is HandType.Split && GameplayPlayerMainHandPanel.Controls.Count > 1)
            {
                GameplayPlayerMainHandPanel.Controls.RemoveAt(1);  
            }
        }

        private void AlterGuiAfterHit(object sender, CardAddedEventArgs e)
        {
            FlowLayoutPanel targetPanel;
            Label targetScoreLabel; 
            string targetScoreLabelTextPreamble = null;

            if (sender is DealerHand)
            {
                targetPanel = GameplayDealerHandPanel;
                targetScoreLabel = GameplayDealerScoreLbl; 
                targetScoreLabelTextPreamble = MessageManager.DEFAULT_DEALER_SCORE_LABEL_TEXT;

                if (e.Hand.Status is HandStatus.Blackjack || e.Hand.Status is HandStatus.Busted || e.Hand.Status is HandStatus.Standing)
                {
                    targetPanel.Controls.Add(CreateCardPictureBoxUsingCard(e.Hand.MostRecentlyDealtCard));
                    return; 
                }
            }
            else 
            {
                targetPanel = CurrentPlayerPanel;
                targetScoreLabel = CurrentPlayerScoreLbl; 
                targetScoreLabelTextPreamble = MessageManager.DEFAULT_PLAYER_SCORE_LABEL_TEXT;
            }

            targetPanel.Controls.Add(CreateCardPictureBoxUsingCardAddedEventArgs(e));
            targetScoreLabel.Text = $"{targetScoreLabelTextPreamble} {e.Hand.Score}"; 
        }

        private void AlterGuiChipAmountsAfterBlackjackEntityChipAmountChanges(object sender, BlackjackEntityChipAmountChangedEventArgs e)
        {
            if (sender is Dealer dealer)
            {
                GameplayDealerChipsLbl.Text = $"{MessageManager.DEFAULT_DEALER_CHIPS_LABEL_TEXT} {dealer.ChipAmount:C}";
            }
            else if (sender is Player player)
            {
                GameplayPlayerChipsLbl.Text = $"{MessageManager.DEFAULT_PLAYER_CHIPS_LABEL_TEXT} {player.ChipAmount:C}";
            }
        }

        private void AlterGuiToShowdownPlayerHands(object sender, EventArgs e)
        {
            ShowdownOccurredEventArgs showdownOccurredEventArgs = e as ShowdownOccurredEventArgs;
            RevealDealerDownCard(showdownOccurredEventArgs);
            AnimateShowdowns(showdownOccurredEventArgs);
            ToggleGuiButtonFunctionalityAfterShowdowns();
        }

        private async Task AnimateShowdown(PlayerHand playerHand, BlackjackEntity winner) 
        {
            if (winner is Dealer)
            {
                GameplayDealerHandPanel.BackColor = Color.Green;
                CurrentPlayerPanel.BackColor = Color.Red;
                MessageManager.ShowdownNormalDealerWin(playerHand, "");
            }
            else //FIX UP THE MESSAGING LOGIC LATER!!
            {
                CurrentPlayerPanel.BackColor = Color.Green;
                GameplayDealerHandPanel.BackColor = Color.Red;
                MessageManager.ShowdownNormalPlayerWin(playerHand, "");
            }

            await Task.Delay(3000);
            GameplayDealerHandPanel.BackColor = Color.Transparent;
            CurrentPlayerPanel.BackColor = Color.Transparent;
            await Task.Delay(200); 
        }

        private async void AnimateShowdowns(ShowdownOccurredEventArgs e)
        {
            byte handIndex; 
            PlayerHand[] showdownHands = e.HandsInShowdownOrder; 

            for (handIndex = 0; handIndex < showdownHands.Length; handIndex++)
            {
                CurrentPlayerHand = showdownHands[handIndex]; 

                if (CurrentPlayerHand.HandType is HandType.Main)
                {
                    await AnimateShowdown(CurrentPlayerHand, e.PlayerMainHandWinner);
                }
                else
                {
                    await AnimateShowdown(CurrentPlayerHand, e.PlayerSplitHandWinner);
                }
            }
        }

        private PictureBox CreateCardPictureBoxUsingCard(Card card)
        {
            return new PictureBox
            {
                Image = Image.FromFile($@"{CARD_IMAGES_PATH}\{card.CardPhotoName}"),
                SizeMode = PictureBoxSizeMode.StretchImage,
                Size = new Size(80, 120),
                Margin = new Padding(5),
            };
        }

        private PictureBox CreateCardPictureBoxUsingCardAddedEventArgs(CardAddedEventArgs e)
        {
            return new PictureBox 
            {
                Image = Image.FromFile($@"{CARD_IMAGES_PATH}\{e.CardPhotoName}"), 
                SizeMode = PictureBoxSizeMode.StretchImage,
                Size = new Size(80, 120),
                Margin = new Padding(5),
            };
        }

        private void IntroBackgroundPictureLinkLbl_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo
            {
                FileName = "https://www.freepik.com/free-photo/overhead-view-casino-chips-stack-green-poker-table_2914492.htm#fromView=search&page=1&position=34&uuid=c5ee71db-78cd-4fa5-b44a-909c12bcfdb1&query=blackjack+felt",
                UseShellExecute = true
            });
        }

        private void IntroNewGameBtn_Click(object sender, EventArgs e)
        {
            ToggleGuiButtonFunctionalityForPlacingBets(sender, e);
            GameController.CreateGame();
            GameController.SubscribeToPlayerDrawingHandSetEvents(new BlackjackEntitiesController.AlterFrontendCurrentPlayerHandReferenceAfterPlayerDrawingHandIsSet(AlterCurrentPlayerHandAfterPlayerHandIsSet));
            GameController.SubscribeToBlackjackEntityBankruptsEventArgs(new BlackjackEntitiesController.AlterGuiAfterEntityBankruptsDelegate(AlterGuiAfterEntityBankrupts));
            GameController.SubscribeToBlackjackEntityChipAmountChangedEvents(new BlackjackEntityChipAmountChangedEventArgs.ChangeEntityChipAmountGuiControlDelegate(AlterGuiChipAmountsAfterBlackjackEntityChipAmountChanges), new BlackjackEntityChipAmountChangedEventArgs.ChangeEntityChipAmountGuiControlDelegate(AlterGuiChipAmountsAfterBlackjackEntityChipAmountChanges));
            GameController.SubscribeHandsToCardAdditionEvents(new CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate(AlterGuiAfterHit), new CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate(AlterGuiAfterHit), new CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate(AlterGuiAfterHit));
            GameController.SubscribeToInsuranceBetBecomesPossibleEvents(new InsuranceManager.AlterGuiAfterInsuranceBetBecomesPossible(ToggleGuiButtonFunctionalityForPlacingBets));
            GameController.SubscribeToInsuranceBetIsDeclinedEvents(new InsuranceManager.AlterGuiAfterInsuranceBetIsDeclinedDelegate(ToggleGuiButtonFunctionalityForDrawingRounds));
            GameController.SubscribeToPlayerPlacesBetEvents(new Player.AlterGuiAfterPlayerPlacesBetDelegate(ToggleGuiButtonFunctionalityForDrawingRounds));
            GameController.SubscribeToShowdownOccurredEvents(new ShowdownOccurredEventArgs.DisableGuiInputButtonsAfterShowdownEventDelegate(AlterGuiToShowdownPlayerHands));
            GameplayMessageAndPromptLbl.Text = MessageManager.GREETING_PROMPT;
            GameplayDealerChipsLbl.Text = $"{MessageManager.DEFAULT_DEALER_CHIPS_LABEL_TEXT} {BlackjackEntity.INITIAL_CHIP_AMOUNT:C}.";
            GameplayPlayerChipsLbl.Text = $"{MessageManager.DEFAULT_PLAYER_CHIPS_LABEL_TEXT} {BlackjackEntity.INITIAL_CHIP_AMOUNT:C}.";
            SwitchPanels(GameplayPanel);
        }

        private void GameplayInsuranceBetAndYesOrNoBtn_Click(object sender, EventArgs e)
        {
            if (GameController.InsuranceBetPossible)
            {
                if (GameController.TryToPlaceInsuranceBet(GameplayBetAndYesOrNoTextBox.Text))
                {
                    GameController.CheckEntitiesForBlackjackBeforeDrawingRound();
                }
            }
            else if (GameController.TryToPlaceMainBetAndDeal(GameplayBetAndYesOrNoTextBox.Text))
            {
                if (!GameController.InsuranceBetPossible)
                {
                    GameController.CheckEntitiesForBlackjackBeforeDrawingRound();
                }
            }

            GameplayBetAndYesOrNoTextBox.Text = "";
        }

        private void ResetFrontendAfterShowdown(object sender, EventArgs e)
        {
            GameplayDealerHandPanel.Controls.Clear();
            GameplayDealerScoreLbl.Text = MessageManager.DEFAULT_DEALER_SCORE_LABEL_TEXT;
            GameplayMessageAndPromptLbl.Text = MessageManager.GREETING_PROMPT;
            GameplayPlayerMainHandPanel.Controls.Clear();
            GameplayPlayerMainHandScoreLbl.Text = MessageManager.DEFAULT_PLAYER_SCORE_LABEL_TEXT;
            GameplayPlayerSplitHandPanel.Controls.Clear();
            GameplayPlayerSplitHandScoreLbl.Text = MessageManager.DEFAULT_PLAYER_SCORE_LABEL_TEXT;
            ToggleGuiButtonFunctionalityForPlacingBets(null, EventArgs.Empty);

            GameController.SubscribeHandsToCardAdditionEvents(new CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate(AlterGuiAfterHit), new CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate(AlterGuiAfterHit), new CardAddedEventArgs.UpdateGuiUponCardAdditionEventDelegate(AlterGuiAfterHit));
            GameController.SubscribeToPlayerDrawingHandSetEvents(new BlackjackEntitiesController.AlterFrontendCurrentPlayerHandReferenceAfterPlayerDrawingHandIsSet(AlterCurrentPlayerHandAfterPlayerHandIsSet));
        }

        private void RevealDealerDownCard(ShowdownOccurredEventArgs e)
        {
            byte dealerUpCardsIndex;

            GameplayDealerHandPanel.Controls.Clear();
            GameplayDealerHandPanel.Controls.Add(CreateCardPictureBoxUsingCard(e.DealerHand.UpCards[0]));
            GameplayDealerHandPanel.Controls.Add(CreateCardPictureBoxUsingCard(e.DealerDownCard));

            for (dealerUpCardsIndex = 1; dealerUpCardsIndex < e.DealerHand.UpCards.Count; dealerUpCardsIndex++)
            {
                GameplayDealerHandPanel.Controls.Add(CreateCardPictureBoxUsingCard(e.DealerHand.UpCards[dealerUpCardsIndex]));
            }

            GameplayDealerScoreLbl.Text = $"{MessageManager.DEFAULT_DEALER_SCORE_LABEL_TEXT} {e.DealerHand.Score}";
        }

        private void ToggleGuiButtonFunctionalityAfterShowdowns()
        {
            GameplayDoubleDownBtn.Enabled = false;
            GameplayHitBtn.Enabled = false;
            GameplayBetAndYesOrNoBtn.Enabled = false;
            GameplayNextBetOrHandBtn.Enabled = true;
            GameplaySplitBtn.Enabled = false;
            GameplayStandBtn.Enabled = false;
            GameplayerSurrenderBtn.Enabled = false;
        }
        private void ToggleGuiButtonFunctionalityForPlacingBets(object sender, EventArgs e)
        {
            GameplayDoubleDownBtn.Enabled = false;
            GameplayHitBtn.Enabled = false;
            GameplayBetAndYesOrNoBtn.Enabled = true;
            GameplayNextBetOrHandBtn.Enabled = false;
            GameplaySplitBtn.Enabled = false;
            GameplayStandBtn.Enabled = false;
            GameplayerSurrenderBtn.Enabled = false;
        }
        private void ToggleGuiButtonFunctionalityForDrawingRounds(object sender, EventArgs e)
        {
            GameplayDoubleDownBtn.Enabled = true;
            GameplayHitBtn.Enabled = true;
            GameplayBetAndYesOrNoBtn.Enabled = false;
            GameplayNextBetOrHandBtn.Enabled = false;
            GameplaySplitBtn.Enabled = true;
            GameplayStandBtn.Enabled = true;
            GameplayerSurrenderBtn.Enabled = true;
        }
    }
}