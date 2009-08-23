object Form1: TForm1
  Left = 265
  Top = 258
  Width = 743
  Height = 411
  Caption = 'Bakal'#225#345'sk'#225' pr'#225'ce - Fric Ji'#345#237' - 2009'
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  Menu = MainMenu1
  OldCreateOrder = False
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 13
  object Label1: TLabel
    Left = 8
    Top = 8
    Width = 71
    Height = 13
    Caption = 'V'#253'b'#283'r za'#345#237'zen'#237':'
  end
  object Label2: TLabel
    Left = 248
    Top = 8
    Width = 51
    Height = 13
    Caption = 'Typ lokalit:'
  end
  object Label3: TLabel
    Left = 248
    Top = 152
    Width = 136
    Height = 13
    Caption = 'Vybran'#233' m'#237'stnosti v syst'#233'mu:'
  end
  object Label4: TLabel
    Left = 8
    Top = 152
    Width = 187
    Height = 13
    Caption = 'Za'#345#237'zen'#237' v syst'#233'mu: (Pouze pro maz'#225'n'#237')'
  end
  object Label5: TLabel
    Left = 16
    Top = 0
    Width = 3
    Height = 13
  end
  object Label6: TLabel
    Left = 8
    Top = 304
    Width = 40
    Height = 13
    Caption = 'Zkratka:'
  end
  object Label7: TLabel
    Left = 8
    Top = 336
    Width = 31
    Height = 13
    Caption = 'Barva:'
  end
  object Label8: TLabel
    Left = 192
    Top = 304
    Width = 40
    Height = 13
    Caption = 'Materi'#225'l:'
  end
  object Label10: TLabel
    Left = 376
    Top = 304
    Width = 89
    Height = 13
    Caption = 'Roz'#353'i'#345'uj'#237'c'#237' lokalita:'
  end
  object DevicesList: TListBox
    Left = 8
    Top = 32
    Width = 225
    Height = 113
    ItemHeight = 13
    TabOrder = 0
    OnClick = DevicesListClick
  end
  object LocationsList: TListBox
    Left = 248
    Top = 32
    Width = 225
    Height = 113
    ItemHeight = 13
    TabOrder = 1
    OnClick = LocationsListClick
  end
  object LocationInstancesList: TListBox
    Left = 248
    Top = 176
    Width = 225
    Height = 113
    ItemHeight = 13
    TabOrder = 2
    OnClick = LocationInstancesListClick
  end
  object DevicesInstancesList: TListBox
    Left = 8
    Top = 176
    Width = 225
    Height = 113
    ItemHeight = 13
    TabOrder = 3
    OnClick = DevicesInstancesListClick
  end
  object ListBox1: TListBox
    Left = 488
    Top = 32
    Width = 241
    Height = 257
    ItemHeight = 13
    Items.Strings = (
      'a) Vytvo'#345'en'#237' mal'#233' ontologie:'
      '   1. Vyberte za'#345#237'zen'#237
      '   2.Vyberte typ lokality'
      '   3.Vyberte m'#237'stnost v syst'#233'mu,'
      '      nebo zvolte novou m'#237'stnost'
      '   4.V menu Soubor vytvo'#345'te ontologii za'#345#237'zen'#237
      '   5.Ve slo'#382'ce temp naleznete ontologii za'#345#237'zen'#237
      'b)P'#345'id'#225'n'#237' za'#345#237'zen'#237' do syst'#233'mu'
      '   1.Vyberte za'#345#237'zen'#237
      '   2.Vyberte lokalitu'
      '   3.Vyberte m'#237'stnost v syst'#233'mu,'
      '      nebo zvolte novou m'#237'stnost'
      '   4.V menu Soubor p'#345'idejte za'#345#237'zen'#237
      'c)Maz'#225'n'#237' za'#345#237'zen'#237
      '   1.Vyberte typ za'#345#237'zen'#237' pro maz'#225'n'#237'   '
      '   2.Vyberte za'#345#237'zen'#237' v syst'#233'mu pro odebr'#225'n'#237
      '   3.V menu Soubor odeberte za'#345#237'zen'#237
      '  ')
    TabOrder = 4
  end
  object Edit1: TEdit
    Left = 56
    Top = 296
    Width = 121
    Height = 21
    TabOrder = 5
    Text = 'Pul'#357#225'k'
  end
  object Edit2: TEdit
    Left = 56
    Top = 328
    Width = 121
    Height = 21
    TabOrder = 6
    Text = 'B'#237'l'#253
  end
  object Edit3: TEdit
    Left = 240
    Top = 296
    Width = 121
    Height = 21
    TabOrder = 7
    Text = 'Kovov'#253
  end
  object Edit5: TEdit
    Left = 472
    Top = 296
    Width = 121
    Height = 21
    TabOrder = 8
    Text = 'V rohu'
  end
  object xmlonto: TXMLDocument
    Options = [doNodeAutoCreate, doNodeAutoIndent, doAttrNull, doAutoPrefix]
    Left = 88
    DOMVendorDesc = 'MSXML'
  end
  object MainMenu1: TMainMenu
    Left = 56
    object Soubor1: TMenuItem
      Caption = 'Soubor'
      object Pidejzazen1: TMenuItem
        Caption = 'P'#345'idat za'#345#237'zen'#237
        OnClick = Pidejzazen1Click
      end
      object Smazazen1: TMenuItem
        Caption = 'Odebrat za'#345#237'zen'#237
        OnClick = Smazazen1Click
      end
      object VytvormaleOWL1: TMenuItem
        Caption = 'Vytvo'#345'en'#237' ontologie za'#345#237'zen'#237
        OnClick = VytvormaleOWL1Click
      end
      object Konec1: TMenuItem
        Caption = 'Konec'
        OnClick = Konec1Click
      end
    end
    object Info1: TMenuItem
      Caption = 'Help'
      OnClick = Info1Click
    end
  end
  object xmlgenericonto: TXMLDocument
    Options = [doNodeAutoCreate, doNodeAutoIndent, doAttrNull, doAutoPrefix]
    Left = 120
    DOMVendorDesc = 'MSXML'
  end
end
