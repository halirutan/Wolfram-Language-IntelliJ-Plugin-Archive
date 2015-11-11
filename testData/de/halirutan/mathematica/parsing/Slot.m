(* Slots & SlotSequences *)
#&;
#2&;
##&;
##1&;

(* Simple Association Slots *)
#Test&;
#"Test"&;
{#"Test", #"Test2"}&;

(* Function-call like Slot expressions *)
#["Test"]&;
#[Test]&;

(* Yo Dawg, I heard you like Slots... *)
#[#Test]&;
#[#"Test"]&;
#[#Test&, #"Test"&]&;
#Test[#, #"Test"]&;
#"Test"[#Test&, #]&;