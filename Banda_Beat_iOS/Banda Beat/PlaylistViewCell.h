//
//  PlaylistViewCell.h
//  Banda Beat
//
//  Created by iker on 15/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PlaylistViewCell : UITableViewCell
@property(strong,nonatomic) IBOutlet UILabel *name;
@property(strong,nonatomic) IBOutlet UILabel *songs;
@end
